package com.stacktrace.yo.dagen.igdb

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.stacktrace.yo.dagen.igdb.engine.IGDBEngine
import com.stacktrace.yo.dagen.old.core.Protocol.Start

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
