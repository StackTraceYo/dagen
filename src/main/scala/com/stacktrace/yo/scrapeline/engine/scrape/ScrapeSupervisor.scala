package com.stacktrace.yo.scrapeline.engine.scrape

import akka.actor.{Actor, ActorLogging}
import com.stacktrace.yo.scrapeline.engine.core.Engine

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class ScrapeSupervisor(engine: Engine) extends Actor with ActorLogging {

  override def receive: PartialFunction[Any, Unit] = {

    case _ =>

  }
}
