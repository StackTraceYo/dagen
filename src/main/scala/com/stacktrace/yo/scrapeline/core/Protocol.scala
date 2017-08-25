package com.stacktrace.yo.scrapeline.core

import akka.actor.ActorRef

/**
  * Created by Stacktraceyo on 8/24/17.
  */
object Protocol {

  //for reporting
  case class Report()

  //starts the system at the phase passed in or whatever the default is
  case class Start(phase: String = "")

  //pipeline is finished
  case class Finished()

  //Begin Phase
  case class StartPhase()

  //Next Phase
  case class NextPhase(previous: String)

  //Phase Is completed with name @name
  case class PhaseFinished(name: String)

  //Phase Is partially done and next one can start in parallel
  case class PhasePartial(name: String)

  //Delegate from pipeline
  case class StartDelegate(pipeline: ActorRef)

  //Worker Is Working
  case class Progress()

}
