package com.stacktrace.yo.scrapeline.engine.json

import akka.http.scaladsl.model.HttpResponse

/**
  * Created by Stacktraceyo on 9/6/17.
  */
object JSONProtocol {

  type JSONContent = String

  type JSONContentCallBack = JSONContent => Unit

  case class RequestUrlAndCall(url: String, callback: JSONContentCallBack)

  case class RequestUrl(url: String)

  case class Requested(url: String, doc: HttpResponse)

}
