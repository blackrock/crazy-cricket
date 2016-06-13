package com.bfm.acs.crazycricket

import java.util.Properties
import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer}

/**
  * Created by Oscar on 5/19/16.
  */
case class KafkaHelper(brokerAddr: String) {
  import KafkaHelper._
  private val props = new Properties()
  props.put("bootstrap.servers", brokerAddr)
  props.put("acks", "all")
  props.put("retries", "0")
  props.put("batch.size", "16384")
  props.put("linger.ms", "1")
  props.put("buffer.memory", "33554432")

  lazy val producerProps = {
    props.put("key.serializer", KEY_SERIALIZER)
    props.put("value.serializer", VAL_SERIALIZER)
    props
  }
  lazy val consumerProps = {
    props.put("group.id", "test")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("session.timeout.ms", "30000")
    props.put("key.deserializer", KEY_DESERIALIZER)
    props.put("value.deserializer", VAL_DESERIALIZER)
    props
  }
}

object KafkaHelper {
  val KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer"
  val VAL_SERIALIZER = "org.apache.kafka.common.serialization.ByteArraySerializer"
  val KEY_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer"
  val VAL_DESERIALIZER = "org.apache.kafka.common.serialization.ByteArrayDeserializer"

  def pushToKafka(topic: String,data: Seq[Array[Byte]],producer: KafkaProducer[String,Array[Byte]]) = {
    println(s"Pushing ${data.length} messages to topic $topic")
    data
      .zipWithIndex
      .foreach{
        case (bytes,index) => {
          producer.send(new ProducerRecord[String,Array[Byte]](topic,index.toString,bytes))
        }
      }
  }
}
