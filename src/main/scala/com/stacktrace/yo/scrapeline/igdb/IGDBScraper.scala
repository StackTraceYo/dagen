package com.stacktrace.yo.scrapeline.igdb

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.stacktrace.yo.scrapeline.core.Protocol.Start
import com.stacktrace.yo.scrapeline.igdb.engine.IGDBEngine

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

/**
  * Created by Stacktraceyo on 8/18/17.
  */
object IGDBScraper extends App {

  implicit val as: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = as.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()


  val controller = as.actorOf(Props(new IGDBEngine()))
  controller ! Start("GameListPipeline")
}
