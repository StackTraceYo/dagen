package com.stacktrace.yo.dagen.igdb.pipeline.extract

import akka.actor.{ActorRef, Props}
import com.stacktrace.yo.dagen.igdb.pipeline.extract.actor.GameDataExtractionDelegate
import com.stacktrace.yo.dagen.old.core.IGDBAPIClient
import com.stacktrace.yo.dagen.old.core.Protocol._
import com.stacktrace.yo.dagen.old.core.pipeline.PipelineActor
import org.stacktrace.yo.igdb.client.IGDBClient

/**
  * Created by Stacktraceyo on 8/18/17.
  */
class GameDetailExtractionPipeline extends PipelineActor {

  var controller: ActorRef = _

  def client: IGDBClient = IGDBAPIClient.getClient


  override def receive: Receive = {
    case StartPhase() =>
      controller = sender
      val supervisor = context.actorOf(Props(new GameDataExtractionDelegate()))
      supervisor ! StartDelegate(self)
    //      sender() ! PhaseFinished("GameDetailExtractionPipeline")
    //      self ! PoisonPill
    case Working() =>
      controller ! StartDownstream("GameDetailExtractionPipeline")
  }


  override def canSkip: Boolean = {
    false
  }


}
