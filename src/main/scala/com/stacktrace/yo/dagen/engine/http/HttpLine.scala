package com.stacktrace.yo.dagen.engine.http

import akka.actor.{ActorSystem, Props}
import com.stacktrace.yo.dagen.engine.core.definitions.HttpDefinition
import com.stacktrace.yo.dagen.engine.core.engine.Engine
import com.stacktrace.yo.dagen.engine.core.protocol.EngineProtocol.Begin
import com.stacktrace.yo.dagen.engine.http.HttpRequestProtocol.{JSONContentCallBack, RequestUrlAndCall}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
abstract class HttpLine(implicit as: ActorSystem) extends HttpDefinition {

  private val httpLineEngine = as.actorOf(Props(new Engine(this)))

  override def begin(): Unit = {
    httpLineEngine ! Begin()
  }

  override def requestApiAndCall(url: String, pipe: JSONContentCallBack): Unit = {
    httpLineEngine ! RequestUrlAndCall(url, pipe)
  }

}
