package com.stacktrace.yo.dagen.old.core

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
  case class PipelineFinished()

  //Begin Phase
  case class StartPhase()

  //Next Phase
  case class NextPhase(previous: String)

  //Phase Is completed with name @name
  case class PhaseFinished(name: String)

  //Phase Is partially done and next one can start in parallel
  case class StartDownstream(name: String)

  //Downstream phases which started before upstream completion can be made aware their upstream is done
  case class UpstreamFinished()

  //Delegate from pipeline
  case class StartDelegate(pipeline: ActorRef)

  //Worker Is Working
  case class Working()

}
