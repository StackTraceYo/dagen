package com.stacktrace.yo.scrapeline.igdb

import com.stacktrace.yo.scrapeline.igdb.pipeline.CreateVideoGameIdListPipeline

import scala.language.postfixOps

/**
  * Created by Stacktraceyo on 8/18/17.
  */
object IGDBScraper extends App {


  val videoGameList = new CreateVideoGameIdListPipeline().buildAndRun
  println("Finished Video Game Name List Flow..")
}
