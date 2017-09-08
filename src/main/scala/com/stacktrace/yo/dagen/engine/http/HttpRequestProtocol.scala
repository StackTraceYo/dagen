package com.stacktrace.yo.dagen.engine.http

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
object HttpRequestProtocol {

  type JSONContent = String

  type JSONContentCallBack = JSONContent => Unit

  case class RequestUrlAndCall(url: String, callback: JSONContentCallBack)

  case class RequestAndCall(request: HttpRequest, callback: JSONContentCallBack)

  case class Request(request: HttpRequest)

  case class ResponseFromRequest(request: HttpRequest, doc: HttpResponse)

}
