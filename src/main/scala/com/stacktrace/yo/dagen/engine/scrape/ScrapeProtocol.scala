package com.stacktrace.yo.dagen.engine.scrape

import net.ruippeixotog.scalascraper.model.Document

/**
  * Created by Stacktraceyo on 9/6/17.
  */
object ScrapeProtocol {

  type ScrapedContent = Document

  type ScrapedContentCallBack = ScrapedContent => Unit

  case class ScrapeUrlAndCall(url: String, callback: ScrapedContentCallBack)

  case class BeginScrape(url: String)

  case class Scraped(url: String, doc: ScrapedContent)


}
