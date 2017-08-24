package com.stacktrace.yo.scrapeline.core

import akka.actor.ActorRef

/**
  * Created by Stacktraceyo on 8/24/17.
  */
object Protocol {

  case class Report()

  case class Start(phase: String = "")

  case class Finished()

  case class StartPhase()

  case class NextPhase(previous: String)

  case class PhaseFinished(name: String)

  case class StartSupervisor(pipelin: ActorRef)

}
