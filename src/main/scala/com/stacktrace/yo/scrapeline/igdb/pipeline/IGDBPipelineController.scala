package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.time.Instant

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import com.stacktrace.yo.scrapeline.igdb.pipeline.IGDBPipelineController._

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Ahmad on 8/23/2017.
  */
class IGDBPipelineController(implicit executionContext: ExecutionContext) extends Actor with ActorLogging {

  var phasesRunning: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var phasesCompleted: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  val phases: mutable.HashMap[String, (Long, Long)] = mutable.HashMap[String, (Long, Long)]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 1000 millis, self, Report())


  override def receive: Receive = {
    case Start(phase) =>
      val p1 = getPipeline(phase)
      handle(phase)
      p1 ! StartPhase()
    case PhaseFinished(name: String) =>
      phases.get(name) match {
        case Some(v) =>
          phases.put(name, (v._1, Instant.now().getEpochSecond))
          phasesCompleted += name
          phasesRunning = phasesRunning.filter(_ != name)
          self ! NextPhase(name)
        case None =>
          log.warning("Phase {} Not found...", name)
      }
    case NextPhase(fin: String) =>
      fin match {
        case "CreateVideoGameIdListPipeline" =>
          val p2 = context.actorOf(Props(new GetGameDetailPipeline(self)))
          phasesRunning += "GetGameDetailPipeline"
          phases.put("GetGameDetailPipeline", (Instant.now().getEpochSecond, 0))
          self ! Report()
          p2 ! StartPhase()
      }

    case Report() => {
      log.info("Currenly Running : {}", phasesRunning.mkString(","))
      log.info("Completed: {}", phasesCompleted.mkString(","))
    }
  }

  private def handle(phase: String): Unit = {
    phasesRunning += phase
    phases.put(phase, (Instant.now().getEpochSecond, 0))
    self ! Report()
  }

  private def getPipeline(name: String): ActorRef = {
    val actor = context.actorOf(Props(
      name match {
        case "CreateVideoGameIdListPipeline" => new CreateVideoGameIdListPipeline
        case "GetGameDetailPipeline" => new GetGameDetailPipeline(self)
        case _ => new CreateVideoGameIdListPipeline
      }
    ))
    actor
  }


}

object IGDBPipelineController {

  case class Report()

  case class Start(phase: String = "")

  case class Finished()

  case class StartPhase()

  case class NextPhase(previous: String)

  case class PhaseFinished(name: String)

}
