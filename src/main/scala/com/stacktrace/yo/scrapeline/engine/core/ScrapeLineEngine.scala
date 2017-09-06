package com.stacktrace.yo.scrapeline.engine.core

import akka.actor.{ActorRef, Props}
import com.stacktrace.yo.scrapeline.engine.Scrapeline
import com.stacktrace.yo.scrapeline.engine.core.EngineProtocol.{Begin, Read, Scrape}
import com.stacktrace.yo.scrapeline.engine.scrape.{ScrapeEngine, ScrapeSupervisor}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class ScrapeLineEngine(scrapeline: Scrapeline) extends Engine with ScrapeEngine {


  //  override val fileSourceSupervisor: ActorRef = context.actorOf(Props(new FileSourceSupervisor(this)))
  override val scrapeSupervisor = context.actorOf(Props(new ScrapeSupervisor(this)))

  override def receive: PartialFunction[Any, Unit] = {

    case Begin() =>
      scrapeline.start.foreach {
        case Scrape(url) =>
          log.info("Scrape Url: {}", url)
        case Read(url) =>
      }
  }


}
