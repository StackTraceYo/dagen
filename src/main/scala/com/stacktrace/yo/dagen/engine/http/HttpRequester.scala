package com.stacktrace.yo.dagen.engine.http

import akka.actor.{Actor, ActorRef}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait HttpRequester {


  this: Actor =>

  val requestSupervisor: ActorRef

}
