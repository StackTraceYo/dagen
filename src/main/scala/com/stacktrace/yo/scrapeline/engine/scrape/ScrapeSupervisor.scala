package com.stacktrace.yo.scrapeline.engine.scrape

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.RoundRobinPool
import com.stacktrace.yo.scrapeline.engine.core.Engine
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol._

import scala.collection.concurrent.TrieMap

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class ScrapeSupervisor(engine: Engine) extends Actor with ActorLogging {

  val scrapers: ActorRef =
    context.actorOf(RoundRobinPool(5).props(Props(new ScrapeActor)))
  val callbacks: TrieMap[String, ScrapedContentCallBack] = TrieMap[String, ScrapedContentCallBack]()

  override def receive: PartialFunction[Any, Unit] = {

    case msg@ScrapeUrl(url, callback) =>
      callbacks.put(url, callback)
      scrapers ! BeginScrape(url)
    case msg@Scraped(url: String, doc: ScrapedContent) =>
      callbacks.get(url) match {
        case Some(callback) =>
          callback(doc)
          callbacks.remove(url)
          log.info("Calling and Removing Callback for {}", url)
        case None =>
          log.info("No Callback Found for {}", url)
      }
  }
}
