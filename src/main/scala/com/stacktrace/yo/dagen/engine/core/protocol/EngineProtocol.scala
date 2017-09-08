package com.stacktrace.yo.dagen.engine.core.protocol

import akka.http.scaladsl.model.HttpRequest

/**
  * Created by Stacktraceyo on 9/6/17.
  */
object EngineProtocol {

  sealed trait EngineMessageType

  case class Scrape(url: String) extends EngineMessageType

  case class Read(url: String) extends EngineMessageType

  case class UrlRequest(url: String) extends EngineMessageType

  case class CallHttp(url: HttpRequest) extends EngineMessageType

  case class Begin()

}
