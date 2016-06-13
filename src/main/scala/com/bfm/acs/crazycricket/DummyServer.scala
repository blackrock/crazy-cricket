package com.bfm.acs.crazycricket

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

/**
  * This class creates a simple server that sends back a
  * dummy JSON message for testing purposes, otherwise
  * all the HTTP response messages error out on
  * connection issues. Obviously your own REST API
  * will take it's place.
  *
  * Created by Oscar on 6/3/16.
  */
object DummyServer extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("my-system")

  startServer("localhost",port = 8080) {
    complete(
      """{ "values": [["oscar",2],["dummy",0]]}"""
    )
  }
}
