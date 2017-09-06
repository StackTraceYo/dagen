package com.stacktrace.yo.scrapeline.engine

import com.stacktrace.yo.scrapeline.core.ScrapeClient.jsoup
import com.stacktrace.yo.scrapeline.engine.core.EngineProtocol.EngineMessageType

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait Scrapeline {

  def workerCount = 5

  def begin() : Unit

  def start: List[EngineMessageType]

  def scrape(doc: jsoup.DocumentType): Unit

  def andThenScrape(url: String, pipe: (jsoup.DocumentType) => Unit) : Unit


}
