package com.stacktrace.yo.dagen.engine.core.definitions

import com.stacktrace.yo.dagen.engine.http.HttpRequestProtocol.JSONContentCallBack

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait HttpDefinition extends LineDefinition {


  def beginRead(doc: String): Unit

  def requestApiAndCall(url: String, pipe: JSONContentCallBack): Unit


}
