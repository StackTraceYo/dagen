package com.stacktrace.yo.scrapeline.igdb.pipeline

import akka.actor.{Actor, ActorLogging, Props}
import com.stacktrace.yo.scrapeline.igdb.pipeline.IGDBPipelineController.{PhaseFinished, Start, StartPhase}

/**
  * Created by Ahmad on 8/23/2017.
  */
class IGDBPipelineController extends Actor with ActorLogging {


  override def receive: Receive = {
    case Start() =>
      val p1 = context.actorOf(Props(new CreateVideoGameIdListPipeline()))
      p1 ! StartPhase()
    case PhaseFinished() =>
  }


}

object IGDBPipelineController {

  case class Start()

  case class StartPhase()

  case class PhaseFinished()

}
