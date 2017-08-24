package com.stacktrace.yo.scrapeline.igdb

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.stacktrace.yo.scrapeline.igdb.pipeline.{CreateVideoGameIdListPipeline, GetGameDetailPipeline}

import scala.language.postfixOps

/**
  * Created by Stacktraceyo on 8/18/17.
  */
object IGDBScraper extends App {

  implicit val as = ActorSystem()
  implicit val ec = as.dispatcher
  implicit val mat = ActorMaterializer()


  val videoGameList = new CreateVideoGameIdListPipeline().run()
  val gameDetail = new GetGameDetailPipeline().run()
}
