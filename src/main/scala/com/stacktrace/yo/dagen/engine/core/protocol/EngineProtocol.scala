package com.stacktrace.yo.dagen.engine.core.protocol

/**
  * Created by Stacktraceyo on 9/6/17.
  */
object EngineProtocol {

  sealed trait EngineMessageType

  case class Scrape(url: String) extends EngineMessageType

  case class Read(url: String) extends EngineMessageType

  case class Request(url: String) extends EngineMessageType

  case class Begin()

}
