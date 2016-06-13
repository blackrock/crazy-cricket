package com.bfm.acs.crazycricket

import java.sql.Timestamp

import com.bfm.acs.crazycricket.CrazyCricketProtos.{Game, Player}
import Constants._
/**
  * Created by Oscar on 5/28/16.
  */
abstract class ProtoWrapper[T] {
  def getProto: T
}

case class PlayerWrapper(userId: String, country: String) extends ProtoWrapper[Player] {
  def getProto =
    Player.newBuilder
      .setUserId(userId)
      .setCountry(country)
      .build
}

case class GameWrapper(winner: Player, loser: Player, gameDate: Timestamp, gameType: String) extends ProtoWrapper[Game] {
  def getProto =
    Game.newBuilder
      .setWinner(winner)
      .setLoser(loser)
      .setGameDate(gameDate.getTime)
      .setType(TOPICS_TO_GAME(gameType))
      .build
}
