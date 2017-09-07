package com.stacktrace.yo.scrapeline.engine.core

/**
  * Created by Stacktraceyo on 9/6/17.
  */
object EngineProtocol {

  sealed trait EngineMessageType

  case class Scrape(url: String) extends EngineMessageType

  case class Read(url: String) extends EngineMessageType

  case class Begin()

}
