package com.stacktrace.yo.scrapeline.engine.core.engine

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.stacktrace.yo.scrapeline.engine.core.definitions.{LineDefinition, ScrapeDefinition}
import com.stacktrace.yo.scrapeline.engine.core.protocol.EngineProtocol.{Begin, Read, Scrape}
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.{ScrapeUrlAndCall, ScrapedContentCallBack}
import com.stacktrace.yo.scrapeline.engine.scrape.{ScrapeSupervisor, Scraper}

import scala.concurrent.ExecutionContext

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class Engine(scrapeline: LineDefinition)(implicit as: ActorSystem) extends Actor with ActorLogging with Scraper {

  implicit val ec: ExecutionContext = as.dispatcher
  //  override val fileSourceSupervisor: ActorRef = context.actorOf(Props(new FileSourceSupervisor(this)))
  override var scrapeSupervisor: ActorRef = context.actorOf(Props(new ScrapeSupervisor(this)))

  override def receive: PartialFunction[Any, Unit] = {

    case Begin() =>
      scrapeline.start.foreach {
        case Scrape(url) =>
          log.info("Scraping Url: {}", url)
          self ! ScrapeUrlAndCall(url, scrapeline.asInstanceOf[ScrapeDefinition].beginScrape)
        case Read(url) =>
          log.info("Reading Url: {}", url)
      }
    case msg@ScrapeUrlAndCall(url: String, callback: ScrapedContentCallBack) =>
      scrapeSupervisor ! msg

  }


}
