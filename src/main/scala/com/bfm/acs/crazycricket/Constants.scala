package com.bfm.acs.crazycricket

import com.bfm.acs.crazycricket.CrazyCricketProtos.Game
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by Oscar on 5/28/16.
  */
object Constants {
  val DEFAULT_TEST_DATA = "/sample-games"

  val TEST_GAME = "TEST"
  val LIMITED_OVERS = "LIMITED_OVERS"
  val TWENTY_TWENTY = "TWENTY_TWENTY"
  val OTHER = "OTHER"

  val TOPICS_TO_GAME =
    Map[String,Game.GameType](
      "TEST" -> Game.GameType.TEST,
      "LIMITED_OVERS" -> Game.GameType.LIMITED_OVERS,
      "TWENTY_TWENTY" -> Game.GameType.TWENTY_TWENTY,
      "OTHER" -> Game.GameType.OTHER
    )

  // Required paths
  val LEADERBOARD_PATH = "api/leaderboard"
  val NAT_LEADERBOARD_PATH = "api/national_leaderboard"

  // Test titles
  val TEST_LEADERBOARD = "Test leaderboard call"
  val TEST_NAT_LEADERBOARD = "Test national leaderboard"
  val TEST_LEADERBOARD_PARAMS = "Test leaderboard call over date range"
  val TEST_NAT_LEADERBOARD_PARAMS = "Test national leaderboard call over date range"

  val DATE_FORMAT = DateTimeFormat.forPattern("yyyyMMdd")
}
