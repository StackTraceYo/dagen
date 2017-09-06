package com.stacktrace.yo.scrapeline.igdb.engine

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import com.stacktrace.yo.scrapeline.core.Protocol._
import com.stacktrace.yo.scrapeline.igdb.pipeline.detail.GameDetailPipeline
import com.stacktrace.yo.scrapeline.igdb.pipeline.extract.GameDetailExtractionPipeline
import com.stacktrace.yo.scrapeline.igdb.pipeline.list.GameListPipeline

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Ahmad on 8/23/2017.
  */
class IGDBEngine(implicit executionContext: ExecutionContext) extends Actor with ActorLogging {

  var phasesRunning: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var phasesCompleted: mutable.ListBuffer[String] = mutable.ListBuffer[String]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 10000 millis, self, Report())


  override def receive: Receive = {
    case Start(phase) =>
      val p1 = getPipeline(phase)
      handle(phase)
      p1 ! StartPhase()
    case PhaseFinished(name: String) =>
      phasesCompleted += name
      phasesRunning = phasesRunning.filter(_ != name)
      self ! NextPhase(name)
      self ! Report()
    case StartDownstream(name: String) =>
      self ! NextPhase(name)
      self ! Report()
    case NextPhase(fin: String) =>
      fin match {
        case "GameListPipeline" =>
          val phase = getPipeline("GameDetailPipeline")
          phase ! StartPhase()
          handle("GameDetailPipeline")
        case "GameDetailPipeline" =>
          val phase = getPipeline("GameDetailExtractionPipeline")
          phase ! StartPhase()
          handle("GameDetailExtractionPipeline")
        case "GameDetailExtractionPipeline" =>
        //          val phase = getPipeline("GameDetailExtractionPipeline")
        //          phase ! StartPhase()
        //          handle("GameDetailPipeline")
      }
    case Report() =>
      log.info("Currenly Running : {}", phasesRunning.mkString(","))
      log.info("Completed: {}", phasesCompleted.mkString(","))
  }

  private def handle(phase: String): Unit = {
    phasesRunning += phase
  }

  private def getPipeline(name: String): ActorRef = {
    val actor = context.actorOf(Props(
      name match {
        case "GameListPipeline" => new GameListPipeline()
        case "GameDetailPipeline" => new GameDetailPipeline()
        case "GameDetailExtractionPipeline" => new GameDetailExtractionPipeline()
        case _ => new GameListPipeline
      }
    ))
    actor
  }


}
