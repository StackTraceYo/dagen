package com.stacktrace.yo.scrapeline.engine.scrape

import akka.actor.{Actor, ActorLogging}
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.{BeginScrape, Scraped, ScrapedContent}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

class ScrapeActor extends Actor with ActorLogging {

  lazy val scrapeBrowser = JsoupBrowser()

  override def receive: Receive = {
    case msg@BeginScrape(url: String) =>
      val oSender = sender
      log.info("Getting {}", url)
      val doc: ScrapedContent = scrapeBrowser.get(url)
      sender ! Scraped(url, doc)
  }
}


