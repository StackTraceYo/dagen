package com.stacktrace.yo.scrapeline.engine.scrape

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.routing.RoundRobinPool
import com.stacktrace.yo.scrapeline.engine.core.Engine
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol._
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeSupervisor.{ProcessScrape, SendNextRequests}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class ScrapeSupervisor(engine: Engine)(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  val scrapers: ActorRef = context.actorOf(RoundRobinPool(5).props(Props(new ScrapeActor)))

  val callbacks: TrieMap[String, ScrapedContentCallBack] = TrieMap[String, ScrapedContentCallBack]()

  val toProcess: mutable.Queue[(String, ScrapedContentCallBack)] = scala.collection.mutable.Queue[(String, ScrapedContentCallBack)]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 5000 millis, self, SendNextRequests())


  override def receive: PartialFunction[Any, Unit] = {

    case msg@ScrapeUrlAndCall(url, callback) =>
      toProcess.enqueue((url, callback))
    case SendNextRequests() =>
      for (i <- 1 to Math.min(toProcess.size, 100 - callbacks.size)) {
        val process = toProcess.dequeue
        self ! ProcessScrape(process._1, process._2)
      }
    case msg@ProcessScrape(url, callback) =>
      callbacks.put(url, callback)
      scrapers ! BeginScrape(url)
    case msg@Scraped(url: String, doc: ScrapedContent) =>
      callbacks.get(url) match {
        case Some(callback) =>
          callback(doc)
          callbacks.remove(url)
        case None =>
          log.info("No Callback Found for {}", url)
      }
  }
}

object ScrapeSupervisor {

  case class SendNextRequests()

  case class ProcessScrape(url: String, callBack: ScrapedContentCallBack)

}



