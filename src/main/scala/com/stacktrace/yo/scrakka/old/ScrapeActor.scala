package com.stacktrace.yo.scrakka.old

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.stacktrace.yo.scrakka.old.ScrapeActor.{BeginScrape, ScrapeContent}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

class ScrapeActor extends Actor with ActorLogging {

  lazy val jsoup = JsoupBrowser()

  override def receive: Receive = {
    case msg@BeginScrape(url: String) =>
      val oSender = sender
      log.info("Getting {}", url)
      val doc = jsoup.get(url)
      sender ! ScrapeContent(doc)
      log.info("Response Returned .. Closing")
      self ! PoisonPill
  }
}

object ScrapeActor {

  case class BeginScrape(url: String)

  case class ScrapeContent(document: Document)

}

