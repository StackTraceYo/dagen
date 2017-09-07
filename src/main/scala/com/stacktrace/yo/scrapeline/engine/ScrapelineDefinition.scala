package com.stacktrace.yo.scrapeline.engine

import com.stacktrace.yo.scrapeline.engine.core.EngineProtocol.EngineMessageType
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.{ScrapedContent, ScrapedContentCallBack}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait ScrapelineDefinition {

  def begin(): Unit

  def start: List[EngineMessageType]

  def beginScrape(doc: ScrapedContent): Unit

  def requestAndCall(url: String, pipe: ScrapedContentCallBack): Unit



}
