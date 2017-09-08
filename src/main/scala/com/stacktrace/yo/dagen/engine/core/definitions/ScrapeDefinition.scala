package com.stacktrace.yo.dagen.engine.core.definitions

import com.stacktrace.yo.dagen.engine.scrape.ScrapeProtocol.{ScrapedContent, ScrapedContentCallBack}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait ScrapeDefinition extends LineDefinition {


  def beginScrape(doc: ScrapedContent): Unit

  def requestAndCall(url: String, pipe: ScrapedContentCallBack): Unit


}
