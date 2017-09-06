package com.stacktrace.yo.scrapeline.engine.file

import akka.actor.Actor

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait FileEngine {

  this : Actor =>


  val fileSourceSupervisor : FileSourceSupervisor




}
