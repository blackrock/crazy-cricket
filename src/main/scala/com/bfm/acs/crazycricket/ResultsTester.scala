package com.bfm.acs.crazycricket

import org.apache.kafka.clients.producer.KafkaProducer
import org.joda.time.DateTime
import scopt.OptionParser
import spray.json._
import com.bfm.acs.crazycricket.Constants._
import scala.util.{Failure, Success, Try}
import scalaj.http.Http
import KafkaHelper._
import TestData._

/**
  * This class connects to the specified REST API and Kafka broker
  * and then sends sample data to the specified Kafka broker,
  * and tests the responses to the HTTP requests described
  * in the documentation.
  *
  * Created by Oscar on 5/28/16.
  */
object ResultsTester {

  /**
    * An implicit for decoding the results.
    */
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val leaderBoardFormat = jsonFormat1(LeaderBoard)
  }
  import MyJsonProtocol._

  val parser = ResultsTesterOptParser

  val appName = this.getClass.getName

  /**
    * A case class representing the expected format of the
    * JSON returned.
    * @param values pairs of userId/country and win counts
    */
  case class LeaderBoard(values: List[(String,Int)])

  /**
    * Prints a failed test result where an exception was thrown.
    * @param title title of the test being run
    * @param e exception caught
    */
  def printFailureException(title: String, e: Throwable) = {
    println(Console.RED + title)
    print(Console.RED + s" - ")
    e.printStackTrace
  }

  /**
    * Prints a failed test result where values were returned.
    * @param title the title of the test being run
    * @param res string with extra information
    */
  def printFailure(title: String, res: String) = {
    println(Console.RED + title)
    println(Console.RED + s" - $res")
  }

  /**
    * Prints a successful test result.
    * @param title title oft he test being run
    * @param res string with extra information
    */
  def printSuccess(title: String, res: String) = {
    println(Console.GREEN + title)
    println(Console.GREEN + s" - $res")
  }

  /**
    * Makes an HTTP request for a LeaderBoard as JSON using at the
    * specified path.
    * @param path the path of the HTTP request
    * @return the LeaderBoard object parsed from the returned JSON
    */
  def getLeaderboard(path: String): LeaderBoard = {
    val reqString = s"http://$path"
    println(s"Making HTTP request to $reqString")
    val response = Http(reqString)
      .asString
      .body
    println(s"Response is $response")
    response
      .parseJson
      .convertTo[LeaderBoard]
  }

  /**
    * Runs a test comparing expected leaderboard values and leadboard values
    * returned by the REST API.
    * @param title title of the test
    * @param path path to the expected location of the resource
    * @param requiredLeaderBoard the LeaderBoard the tests expects to be returned fromt the REST API
    */
  def genericTest(title: String,path: String,requiredLeaderBoard: LeaderBoard) = {
    Try(getLeaderboard(path)) match {
      case Success(l) => {
        if(l == requiredLeaderBoard) printSuccess(title,"passes")
        else printFailure(title,s"$l does not equal $requiredLeaderBoard")
      }
      case Failure(e) => printFailureException(title,e)
    }
  }

  /**
    * Test the all time leaderboard.
    * @param hostname host:port where REST API is running
    * @param requiredLeaderBoard expected return
    */
  def testLeaderboard(hostname: String,requiredLeaderBoard: LeaderBoard) =
    genericTest(TEST_LEADERBOARD,s"$hostname/$LEADERBOARD_PATH",requiredLeaderBoard)

  /**
    * Test the national leaderboard.
    * @param hostname host:port where the REST API is running
    * @param requiredLeaderBoard expected return
    */
  def testNatLeaderboard(hostname: String,requiredLeaderBoard: LeaderBoard) =
    genericTest(TEST_NAT_LEADERBOARD,s"$hostname/$NAT_LEADERBOARD_PATH",requiredLeaderBoard)

  /**
    * Test the learderboard with date parameters.
    * @param hostname host:port where the REST API is running
    * @param requiredLeaderBoard expected return
    * @param start start date for query
    * @param end end date for query
    */
  def testLeaderboard(hostname: String,requiredLeaderBoard: LeaderBoard,start: DateTime, end: DateTime) =
    genericTest(
      TEST_LEADERBOARD_PARAMS,
      s"$hostname/$LEADERBOARD_PATH?start=${start.toString(DATE_FORMAT)}&end=${end.toString(DATE_FORMAT)}",
      requiredLeaderBoard
    )

  /**
    * Test the learderboard with date parameters
    * @param hostname host:port where the REST API is running
    * @param requiredLeaderBoard expected return
    * @param start start date for query
    * @param end end date for query
    */
  def testNatLeaderboard(hostname: String,requiredLeaderBoard: LeaderBoard,start: DateTime, end: DateTime) =
    genericTest(
      TEST_NAT_LEADERBOARD_PARAMS,
      s"$hostname/$NAT_LEADERBOARD_PATH?start=${start.toString(DATE_FORMAT)}&end=${end.toString(DATE_FORMAT)}",
      requiredLeaderBoard
    )

  /**
    * Runner
    * @param parser arg parser
    * @param args command line args
    * @return true if config is parsed from args, false otherwise
    */
  def run(parser: OptionParser[ResultsTesterOptParserConfig],args: Array[String]) = {
    parser.parse(args.toSeq,ResultsTesterOptParserConfig()) match {
      case Some(config) => {
        // Setup a producer to send data to
        val hostname = config.apiLocation
        val kafkaProps = KafkaHelper(config.kafkaBroker).producerProps
        val producer = new KafkaProducer[String,Array[Byte]](kafkaProps)

        println(
          s"""
             |Configurations:
             |  - REST API configured to location $hostname
             |  - Kafka broker configured to ${config.kafkaBroker}
            """.stripMargin)

        // Ensure that the leaderboard is empty
        val emptyLeaderBoard = LeaderBoard(List())
        testLeaderboard(hostname,emptyLeaderBoard)

        // Add some data and then check that the API
        val initialGames =
          Seq(
            GameWrapper(oscar.getProto,sachin.getProto,firstGameDateTS,TEST_GAME),
            GameWrapper(oscar.getProto,shubham.getProto,firstGameDateTS,TEST_GAME),
            GameWrapper(sachin.getProto,shubham.getProto,firstGameDateTS,TEST_GAME)
          )
        pushToKafka(TEST_GAME,initialGames.map(_.getProto.toByteArray),producer)
        val firstDateLeaderBoard =
          LeaderBoard(
            List(
              (oscar.userId,2),
              (sachin.userId,1),
              (shubham.userId,0)
            )
          )
        testLeaderboard(hostname,firstDateLeaderBoard)

        val firstDateNatLeaderBoard =
          LeaderBoard(
              List(
                (ENGLAND,2),
                (INDIA,1)
              )
            )
        testNatLeaderboard(hostname,firstDateNatLeaderBoard)

        // Add some more games at a different date
        val moreGames =
          Seq(
            GameWrapper(imran.getProto,sachin.getProto,secondGameDateTS,TEST_GAME),
            GameWrapper(imran.getProto,shubham.getProto,secondGameDateTS,TEST_GAME),
            GameWrapper(imran.getProto,oscar.getProto,secondGameDateTS,TEST_GAME)
          )
        pushToKafka(TEST_GAME,moreGames.map(_.getProto.toByteArray),producer)

        // Test the new leaderboard
        val secondDateLeaderBoard =
          LeaderBoard(
            List(
              (imran.userId,3),
              (oscar.userId,2),
              (sachin.userId,1),
              (shubham.userId,0)
            )
          )
        testLeaderboard(hostname,secondDateLeaderBoard)

        val secondDateNatLeaderBoard =
          LeaderBoard(
            List(
              (PAKISTAN,3),
              (ENGLAND,2),
              (INDIA,1)
            )
          )
        testNatLeaderboard(hostname,secondDateNatLeaderBoard)

        // Add some date parameters to our query
        testLeaderboard(hostname,firstDateLeaderBoard,firstGameDate.minusDays(1),secondGameDate.minusDays(1))
        testNatLeaderboard(hostname,firstDateNatLeaderBoard,firstGameDate.minusDays(1),secondGameDate.minusDays(1))

        // Add some games to the 20/20 topic
        val twentyTwentyGames =
          Seq(
            GameWrapper(oscar.getProto,sachin.getProto,firstGameDateTS,TWENTY_TWENTY),
            GameWrapper(shubham.getProto,oscar.getProto,secondGameDateTS,TWENTY_TWENTY),
            GameWrapper(imran.getProto,andrew.getProto,firstGameDateTS,TWENTY_TWENTY),
            GameWrapper(andrew.getProto,sachin.getProto,secondGameDateTS,TWENTY_TWENTY),
            GameWrapper(shubham.getProto,imran.getProto,thirdGameDateTS,TWENTY_TWENTY),
            GameWrapper(oscar.getProto,andrew.getProto,thirdGameDateTS,TWENTY_TWENTY)
          )
        pushToKafka(TWENTY_TWENTY,twentyTwentyGames.map(_.getProto.toByteArray),producer)

        val newAllTimeLeaderBoard =
          LeaderBoard(
            List(
              (imran.userId,4),
              (oscar.userId,4),
              (shubham.userId,2),
              (andrew.userId,1),
              (sachin.userId,1)
            )
          )

        val newAllTimeNatLeaderBoard =
          LeaderBoard(
            List(
              (ENGLAND,4),
              (PAKISTAN,4),
              (INDIA,3),
              (USA,1)
            )
          )

        // Test the all time leaderboards
        testLeaderboard(hostname,newAllTimeLeaderBoard)
        testNatLeaderboard(hostname,newAllTimeNatLeaderBoard)

        val thirdDateLeaderBoard =
          LeaderBoard(
            List(
              (oscar.userId,1),
              (shubham.userId,1)
            )
          )

        val thirdDateNatLeaderBoard =
          LeaderBoard(
            List(
              (ENGLAND,1),
              (INDIA,1)
            )
          )

        // Add some data parameters
        testLeaderboard(hostname,thirdDateLeaderBoard,thirdGameDate.minusDays(1),thirdGameDate.plusDays(2))
        testLeaderboard(hostname,thirdDateNatLeaderBoard,thirdGameDate.minusDays(1),thirdGameDate.plusDays(2))

        true
      }
      case None => false
    }
  }

  /**
    * Main method
    * @param args command line args
    */
  def main(args: Array[String]) = {
    run(parser.getParser(appName),args)
    ()
  }
}

/**
  * An configuration for the command line parser.
  * @param apiLocation host:port where REST API is running
  * @param kafkaBroker host:port where Kafka broker is running
  */
case class ResultsTesterOptParserConfig(apiLocation: String = "", kafkaBroker: String = "")

/**
  * An option parser for command line args.
  */
object ResultsTesterOptParser {
  def getParser(appName: String) = {
    new OptionParser[ResultsTesterOptParserConfig](appName) {
      head(appName)
      opt[String]("api-location") required() valueName "<host:port>" action { (x, config) =>
        config.copy(apiLocation = x)
      } text "host:port to direct REST calls to"
      opt[String]("kafka-location") required() valueName "<host:port>" action { (x, config) =>
        config.copy(kafkaBroker = x)
      }
    }
  }
}