package com.stacktrace.yo.scrapeline.igdb.pipeline.extract

import akka.actor.{ActorRef, Props}
import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import com.stacktrace.yo.scrapeline.core.Protocol._
import com.stacktrace.yo.scrapeline.core.pipeline.PipelineActor
import com.stacktrace.yo.scrapeline.igdb.pipeline.extract.actor.GameDataExtractionDelegate
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
