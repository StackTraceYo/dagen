package com.stacktrace.yo.dagen.engine.core.definitions

import com.stacktrace.yo.dagen.engine.core.protocol.EngineProtocol.EngineMessageType

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait LineDefinition {

  def begin(): Unit

  def start: List[EngineMessageType]

}
