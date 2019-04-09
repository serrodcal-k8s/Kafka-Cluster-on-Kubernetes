package com.serrodcal.poc

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("rest-producer")
  implicit val materializer: Materializer = ActorMaterializer()

  val producerConfigs = config.getConfig("akka.kafka.producer")
  val producerSettings = ProducerSettings(producerConfigs, new StringSerializer, new StringSerializer)

  val topic = config.getString("topic")

  // needed for the future flatMap/onComplete in the end
  implicit val executionContextn = system.dispatcher

  implicit val logger = Logging(system, getClass)

  val host = config.getString("server.host")
  val port = config.getString("server.port")

  val bufferSize = config.getString("bufferSize")

  val overflowStrategy = akka.stream.OverflowStrategy.backpressure

  val queue = Source.queue[String](bufferSize.toInt, overflowStrategy)
    .map(message => new ProducerRecord[String, String](topic, message))
    .to(akka.kafka.scaladsl.Producer.plainSink(producerSettings))
    .run()

  val requestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(POST, Uri.Path("/produce"), _, entity, _) => {
      val bodyMessage: Future[String] = Unmarshal(entity).to[String]
      bodyMessage.onComplete{
        case Success(message) => queue offer message
        case Failure(_)  => println("Some error getting message from body")
      }
      Future{HttpResponse(202, entity = "Message sent to Kafka!")}
    }
    case r: HttpRequest => {
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      Future{HttpResponse(404, entity = "Unknown reesource!")}
    }
  }

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] = Http().bind(interface = host, port = port.toInt)
  val bindingFuture: Future[Http.ServerBinding] = serverSource.to(Sink.foreach { connection =>
    println("Accepted new connection from " + connection.remoteAddress)
    connection handleWithAsyncHandler requestHandler
    // this is equivalent to
    // connection handleWith { Flow[HttpRequest] map requestHandler }
  }).run()

  logger.info(s"Server online at http://$host:$port/\n")
  while(true){
    // let it run forever (needed for Docker)
  }
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

}
