package com.stacktrace.yo.scrapeline.igdb

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.stacktrace.yo.scrapeline.igdb.pipeline.CreateVideoGameIdListPipeline

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Stacktraceyo on 8/18/17.
  */
object IGDBScraper extends App {


  implicit val as = ActorSystem()
  implicit val ec = as.dispatcher
  implicit val mat = ActorMaterializer()


  val videoGameList = new CreateVideoGameIdListPipeline().buildAndRun
  Await.result(videoGameList, 10 seconds)
  println("Finished Video Game Name List Flow..")

  as.terminate()
}
