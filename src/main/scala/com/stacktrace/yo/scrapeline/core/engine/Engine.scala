package com.stacktrace.yo.scrapeline.core.engine

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import com.stacktrace.yo.scrapeline.core.Protocol._
import com.stacktrace.yo.scrapeline.igdb.pipeline.{GameDetailPipeline, GameListPipeline}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Ahmad on 8/23/2017.
  */
class Engine(implicit executionContext: ExecutionContext) extends Actor with ActorLogging {

  var phasesRunning: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var phasesCompleted: mutable.ListBuffer[String] = mutable.ListBuffer[String]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 15000 millis, self, Report())


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
    case NextPhase(fin: String) =>
      fin match {
        case "CreateVideoGameIdListPipeline" =>
          val phase = context.actorOf(Props(new GameDetailPipeline()))
          phase ! StartPhase()
          phasesRunning += "GameDetailPipeline"
        case "GameDetailPipeline" =>

      }
    case Report() => {
      log.info("Currenly Running : {}", phasesRunning.mkString(","))
      log.info("Completed: {}", phasesCompleted.mkString(","))
    }
  }

  private def handle(phase: String): Unit = {
    phasesRunning += phase
  }

  private def getPipeline(name: String): ActorRef = {
    val actor = context.actorOf(Props(
      name match {
        case "CreateVideoGameIdListPipeline" => new GameListPipeline()
        case "GameDetailPipeline" => new GameDetailPipeline()
        case _ => new GameListPipeline
      }
    ))
    actor
  }


}
