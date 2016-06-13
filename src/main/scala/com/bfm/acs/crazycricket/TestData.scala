package com.bfm.acs.crazycricket

import java.sql.Timestamp

import org.joda.time.DateTime

/**
  * Static test data.
  *
  * Created by Oscar on 6/3/16.
  */
object TestData {
  // Sample dates
  val firstGameDate = new DateTime(2016,1,1,0,0)
  val secondGameDate = new DateTime(2016,2,1,0,0)
  val thirdGameDate = new DateTime(2016,3,1,0,0)

  val firstGameDateTS = new Timestamp(firstGameDate.getMillis)
  val secondGameDateTS = new Timestamp(secondGameDate.getMillis)
  val thirdGameDateTS = new Timestamp(thirdGameDate.getMillis)

  // Sample players and countries
  val INDIA = "India"
  val PAKISTAN = "Pakistan"
  val ENGLAND = "England"
  val USA = "USA"
  val oscar = PlayerWrapper("oscar",ENGLAND)
  val imran = PlayerWrapper("imran",PAKISTAN)
  val sachin = PlayerWrapper("sachin",INDIA)
  val shubham = PlayerWrapper("shubham",INDIA)
  val andrew = PlayerWrapper("andrew",USA)
}
