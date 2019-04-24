package com.serrodcal.poc

import java.util.Properties

import com.typesafe.config.ConfigFactory
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream


object DoubleFunction extends App {

  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

  val config = ConfigFactory.load()

  val properties: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getString("application.id.config"))
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("bootstrap.servers"))
    p
  }

  val builder = new StreamsBuilder

  val textNumbers: KStream[String, String] = builder.stream[String, String](config.getString("topic.origin"))

  def double(x: Int): Int = x * 2

  val originalAndDouble: KStream[String, String] = textNumbers.map((key, value) => (key, double(value.toInt).toString))

  originalAndDouble.to(config.getString("topic.destination"))

  val streams: KafkaStreams = new KafkaStreams(builder.build(), properties)
  streams.start()

  sys.addShutdownHook {
    streams.close()
  }

}
