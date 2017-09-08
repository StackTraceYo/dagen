package com.stacktrace.yo.dagen.engine.scrape

import akka.actor.{ActorSystem, Props}
import com.stacktrace.yo.dagen.engine.core.definitions.ScrapeDefinition
import com.stacktrace.yo.dagen.engine.core.engine.Engine
import com.stacktrace.yo.dagen.engine.core.protocol.EngineProtocol.Begin
import com.stacktrace.yo.dagen.engine.scrape.ScrapeProtocol.{ScrapeUrlAndCall, ScrapedContentCallBack}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
abstract class ScrapeLine(implicit as: ActorSystem) extends ScrapeDefinition {

  private val scrapeEngine = as.actorOf(Props(new Engine(this)))

  override def begin(): Unit = {
    scrapeEngine ! Begin()
  }

  override def requestAndCall(url: String, pipe: ScrapedContentCallBack): Unit = {
    scrapeEngine ! ScrapeUrlAndCall(url, pipe)
  }


}
