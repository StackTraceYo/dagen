package com.stacktrace.yo.scrapeline.old

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import com.stacktrace.yo.scrapeline.old.HttpRequestSupervisor.SendNextRequests
import com.stacktrace.yo.scrapeline.old.ScrapeActor.{BeginScrape, ScrapeContent}
import net.ruippeixotog.scalascraper.model.Document

import scala.concurrent.duration._
import scala.language.postfixOps

class HttpRequestSupervisor(val urlSet: Set[String]) extends Actor with ActorLogging {

  import context.dispatcher

  val maxRetries = 2
  var numVisited = 0
  var inProcess = 0
  val toProcess = scala.collection.mutable.Queue(urlSet.toList: _*)

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 1000 millis, self, SendNextRequests())

  override def receive: Receive = {
    case SendNextRequests() =>
      if (numVisited < urlSet.size) {
        val sendOut = Math.min(toProcess.size, 10 - inProcess)
        for (i <- 1 to sendOut) {
          val url = toProcess.dequeue
          val reader = context.actorOf(Props(new ScrapeActor()))
          reader ! BeginScrape(url)
          inProcess += 1
        }
      }
    case ScrapeContent(document: Document) => {
      inProcess -= 1
      numVisited += 1
      log.info("Recieved Response {} left in queue, {} in process", toProcess.size, inProcess)

    }
  }
}

object HttpRequestSupervisor {

  case class SendNextRequests()

}
