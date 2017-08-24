package com.stacktrace.yo.scrapeline.igdb.actors

import java.util

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailActor.{GetIds, WriteContent}
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor.WriteNextObjects
import com.stacktrace.yo.scrapeline.igdb.actors.WriteGameDetailActor.{FinishedWrite, WriteGame}
import com.stacktrace.yo.scrapeline.old.HttpRequestSupervisor.SendNextRequests
import org.stacktrace.yo.igdb.model.Game

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

class GameDetailSupervisor(val idSet: Vector[String]) extends Actor with ActorLogging {

  import context.dispatcher

  val idSize: Int = idSet.size

  var numVisited = 0
  var inProcess = 0
  val toProcess = scala.collection.mutable.Queue(idSet: _*)

  var writing = 0
  var numWrote = 0
  val toWrite: mutable.Queue[Game] = scala.collection.mutable.Queue[Game]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 1000 millis, self, SendNextRequests())
  val writeTick: Cancellable = context.system.scheduler.schedule(10000 millis, 1000 millis, self, WriteNextObjects())

  override def receive: Receive = {
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
      log.info("Recieved Response {} left in queue, {} in process", toProcess.size, inProcess)
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
      log.info("Writing {} Done : {} , {} in process", name, idSize - numWrote, writing)
      numWrote += 1
      writing -= 1
    }
  }
}

object GameDetailSupervisor {

  case class SendNextRequests()

  case class WriteNextObjects()

}
