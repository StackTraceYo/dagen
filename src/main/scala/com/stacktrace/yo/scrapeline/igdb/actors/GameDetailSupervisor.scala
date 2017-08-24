package com.stacktrace.yo.scrapeline.igdb.actors

import java.util

import akka.actor.{ActorRef, Cancellable, PoisonPill, Props}
import com.stacktrace.yo.scrapeline.core.Protocol.{Finished, Report, StartSupervisor}
import com.stacktrace.yo.scrapeline.core.pipeline.PipelineActor
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailActor.{GetIds, WriteContent}
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor.WriteNextObjects
import com.stacktrace.yo.scrapeline.igdb.actors.WriteGameDetailActor.{FinishedWrite, WriteGame}
import com.stacktrace.yo.scrapeline.old.HttpRequestSupervisor.SendNextRequests
import org.stacktrace.yo.igdb.model.Game

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

class GameDetailSupervisor(val idSet: Vector[String]) extends PipelineActor {

  import context.dispatcher

  var pipeline: ActorRef = _


  lazy val idSize: Int = idSet.size

  var numVisited = 0
  var inProcess = 0
  lazy val toProcess = scala.collection.mutable.Queue(idSet: _*)

  var writing = 0
  var numWrote = 0
  lazy val toWrite: mutable.Queue[Game] = scala.collection.mutable.Queue[Game]()

  lazy val tick: Cancellable = context.system.scheduler.schedule(0 millis, 1000 millis, self, SendNextRequests())
  lazy val writeTick: Cancellable = context.system.scheduler.schedule(10000 millis, 1000 millis, self, WriteNextObjects())
  lazy val reportTick: Cancellable = context.system.scheduler.schedule(10000 millis, 10000 millis, self, Report())

  override def receive: Receive = {
    case StartSupervisor(pipeline: ActorRef) =>
      this.pipeline = pipeline
      tick
      writeTick
      reportTick
    case SendNextRequests() =>
      if (numVisited < idSet.size) {
        val sendOut = Math.min(toProcess.size, 10 - inProcess)
        for (i <- 1 to sendOut) {
          var list = mutable.ListBuffer[String]()
          for (i <- 1 to 10) {
            list += toProcess.dequeue()
          }
          val reader = context.actorOf(Props(new GameDetailActor()))
          reader ! GetIds(list.mkString(","))
          inProcess += 1
        }
      }
    case msg@WriteContent(doc: util.List[Game]) => {
      inProcess -= 1
      numVisited += 10
      doc.toList.foreach(toWrite.enqueue(_))
    }
    case WriteNextObjects() => {
      if (numWrote < idSize) {
        val sendOut = Math.min(toWrite.size, 10 - writing)
        for (i <- 1 to sendOut) {
          val game = toWrite.dequeue()
          val writer = context.actorOf(Props(new WriteGameDetailActor()))
          writer ! WriteGame(game)
          writing += 1
        }
      }
    }
    case FinishedWrite(name: String) => {
      numWrote += 1
      writing -= 1
    }
    case Report() => {
      log.info("Done Writing: {} , {} in process", idSize - numWrote, writing)
      log.info("{} Response s left in queue, {} in process", toProcess.size, inProcess)
      if (numWrote >= idSize) {
        log.info("Completed Game Detail Writing.. Closing")
        pipeline ! Finished()
        self ! PoisonPill
      }

    }
  }
}

object GameDetailSupervisor {

  case class SendNextRequests()

  case class WriteNextObjects()

}
