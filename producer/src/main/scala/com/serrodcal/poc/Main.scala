package com.serrodcal.poc

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main extends App {

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("producer")
  implicit val materializer: Materializer = ActorMaterializer()

  val producerSettings =
    ProducerSettings(system, new StringSerializer, new StringSerializer)

  val topic = config.getString("topic")

  val done: Future[Done] =
    Source(1 to 100000)
      .map(_.toString)
      .map(value => new ProducerRecord[String, String](topic, value))
      .runWith(Producer.plainSink(producerSettings))

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  done onComplete  {
    case Success(_) => println("Done"); system.terminate()
    case Failure(err) => println(err.toString); system.terminate()
  }

}
