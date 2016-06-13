package com.bfm.acs.crazycricket

import Constants._
import org.apache.kafka.clients.producer.KafkaProducer
import scopt.OptionParser
import TestData._
import KafkaHelper._
/**
  * Created by Oscar on 5/17/16.
  */
object SampleDataProducer {

  val parser = SampleDataProducerOptParser

  val appName = this.getClass.getName

  def run(parser: OptionParser[SampleDataProducerOptParserConfig], args: Array[String]) = {
    parser.parse(args.toSeq,SampleDataProducerOptParserConfig()) match {
      case Some(config) => {
        val producer = new KafkaProducer[String,Array[Byte]](KafkaHelper(config.kafkaBroker).producerProps)
        val testMatches =
          Seq(
            GameWrapper(oscar.getProto,sachin.getProto,firstGameDateTS,TEST_GAME),
            GameWrapper(oscar.getProto,shubham.getProto,secondGameDateTS,TEST_GAME),
            GameWrapper(shubham.getProto,imran.getProto,thirdGameDateTS,TEST_GAME),
            GameWrapper(sachin.getProto,imran.getProto,secondGameDateTS,TEST_GAME),
            GameWrapper(andrew.getProto,imran.getProto,firstGameDateTS,TEST_GAME)
          )
        pushToKafka(TEST_GAME,testMatches.map(_.getProto.toByteArray),producer)
        val twentyTwentyMatches =
          Seq(
            GameWrapper(sachin.getProto,oscar.getProto,firstGameDateTS,TWENTY_TWENTY),
            GameWrapper(sachin.getProto,shubham.getProto,secondGameDateTS,TWENTY_TWENTY),
            GameWrapper(shubham.getProto,imran.getProto,thirdGameDateTS,TWENTY_TWENTY),
            GameWrapper(sachin.getProto,imran.getProto,secondGameDateTS,TWENTY_TWENTY),
            GameWrapper(imran.getProto,andrew.getProto,firstGameDateTS,TWENTY_TWENTY)
          )
        pushToKafka(TWENTY_TWENTY,twentyTwentyMatches.map(_.getProto.toByteArray),producer)
        val limitedOversMatches =
          Seq(
            GameWrapper(shubham.getProto,sachin.getProto,firstGameDateTS,LIMITED_OVERS),
            GameWrapper(shubham.getProto,andrew.getProto,secondGameDateTS,LIMITED_OVERS),
            GameWrapper(shubham.getProto,imran.getProto,thirdGameDateTS,LIMITED_OVERS),
            GameWrapper(oscar.getProto,imran.getProto,secondGameDateTS,LIMITED_OVERS),
            GameWrapper(oscar.getProto,imran.getProto,firstGameDateTS,LIMITED_OVERS)
          )
        pushToKafka(LIMITED_OVERS,limitedOversMatches.map(_.getProto.toByteArray),producer)

        true
      }
      case None => false
    }
  }

  def main(args: Array[String]) = {
    run(parser.getParser(appName),args)
    ()
  }

}

case class SampleDataProducerOptParserConfig(kafkaBroker: String = "")

object SampleDataProducerOptParser {
  def getParser(appName: String) = {
    new OptionParser[SampleDataProducerOptParserConfig](appName) {
      head(appName)
      opt[String]("kafka-broker") required() valueName "<host:port>" action { (x, config) =>
        config.copy(kafkaBroker = x)
      } text "The Kafka broker to connect to"
    }
  }
}


