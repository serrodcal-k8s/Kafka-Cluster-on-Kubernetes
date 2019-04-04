package com.serrodcal.poc

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object Main extends App {

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("consumer")
  implicit val materializer: Materializer = ActorMaterializer()

  val consumerConfigs = config.getConfig("akka.kafka.consumer")
  val consumerSettings = ConsumerSettings(consumerConfigs, new StringDeserializer, new StringDeserializer)

  val topic = config.getString("topic")

  val done = Consumer
          .plainSource(consumerSettings, Subscriptions.topics(topic))
          .runWith(Sink.foreach(println)) // just print each message for debugging

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  done onComplete  {
    case Success(_) => println("Done"); system.terminate()
    case Failure(err) => println(err.toString); system.terminate()
  }

}
