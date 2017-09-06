package com.stacktrace.yo.scrapeline.igdb.actors

import java.util

import akka.actor.{ActorRef, Cancellable, PoisonPill, Props}
import com.stacktrace.yo.scrapeline.core.Protocol.{PipelineFinished, Report, StartDelegate, Working}
import com.stacktrace.yo.scrapeline.core.pipeline.Delegator
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailActor.{GetIds, WriteContent}
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor.WriteNextObjects
import com.stacktrace.yo.scrapeline.igdb.actors.WriteGameDetailActor.{FinishedWrite, WriteGame}
import com.stacktrace.yo.scrapeline.old.HttpRequestSupervisor.SendNextRequests
import org.stacktrace.yo.igdb.model.Game

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

class GameDetailSupervisor(val idSet: Vector[String]) extends Delegator {

  import context.dispatcher

  import scala.collection.JavaConversions._

  var pipeline: ActorRef = _


  lazy val idSize: Int = idSet.size

  //ids visited
  var numVisited = 0
  //requests in process
  var inProcess = 0
  //ids to process
  lazy val toProcess = scala.collection.mutable.Queue(idSet: _*)

  //responses writing
  var writing = 0
  //ids written
  var numWrote = 0
  //lists of games to write
  lazy val toWrite: mutable.Queue[List[Game]] = scala.collection.mutable.Queue[List[Game]]()
  var idsLeftToWrite: Int = idSet.size

  lazy val tick: Cancellable = context.system.scheduler.schedule(0 millis, 10000 millis, self, SendNextRequests())
  lazy val writeTick: Cancellable = context.system.scheduler.schedule(15000 millis, 1000 millis, self, WriteNextObjects())
  lazy val reportTick: Cancellable = context.system.scheduler.schedule(0 millis, 10000 millis, self, Report())

  override def receive: Receive = {
    case StartDelegate(pipeline: ActorRef) =>
      this.pipeline = pipeline
      tick
      writeTick
      reportTick
    case SendNextRequests() =>
      if (numVisited + inProcess < idSet.size) {
        var list = mutable.ListBuffer[String]()
        for (i <- 1 to Math.min(idsLeftToWrite - inProcess, 1000)) { //max thousand ids
          list += toProcess.dequeue()
          inProcess += 1
        }
        val reader = context.actorOf(Props(new GameDetailActor()))
        reader ! GetIds(list.mkString(","))
      }
    case msg@WriteContent(doc: util.List[Game]) => {
      inProcess -= doc.size()
      numVisited += doc.size()
      toWrite enqueue doc.toList
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
    case FinishedWrite(name: String, num: Int) => {
      numWrote += num
      idsLeftToWrite -= num
      writing -= 1
      pipeline ! Working()
    }
    case Report() => {
      log.info("Done Writing: {} , {} in process", idSize - numWrote, writing)
      log.info("{} Responses left in queue, {} in process", toProcess.size, inProcess)
      if (numWrote >= idSize) {
        log.info("Completed Game Detail Writing.. Closing")
        pipeline ! PipelineFinished()
        tick.cancel()
        reportTick.cancel()
        writeTick.cancel()
        self ! PoisonPill
      }
    }
  }
}

object GameDetailSupervisor {

  case class SendNextRequests()

  case class WriteNextObjects()

}
