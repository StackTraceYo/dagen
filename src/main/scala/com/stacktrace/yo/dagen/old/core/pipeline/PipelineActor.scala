package com.stacktrace.yo.dagen.old.core.pipeline

import akka.actor.{Actor, ActorLogging}

/**
  * Created by Stacktraceyo on 8/24/17.
  */
trait PipelineActor extends Actor with ActorLogging {

  _: Actor =>

  def canSkip: Boolean

}
