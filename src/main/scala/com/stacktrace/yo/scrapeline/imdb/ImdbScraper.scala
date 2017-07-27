package com.stacktrace.yo.scrapeline.imdb

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.stacktrace.yo.scrapeline.imdb.pipelines.{MovieListPipeline, MovieNameToIMDBPipeline}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object ImdbScraper extends App {


  implicit val as = ActorSystem()
  implicit val ec = as.dispatcher
  implicit val mat = ActorMaterializer()


  val movieNameList = new MovieListPipeline().buildAndRun
  Await.result(movieNameList, 120 seconds)
  println("Finished Movie Name List Flow..")

  val movieNameToImdbDocument = new MovieNameToIMDBPipeline()
    .getOutput
  Await.result(movieNameToImdbDocument, 120 seconds)
  println("Finished Movie Name To Imdb Url Flow..")
  as.terminate()


}
