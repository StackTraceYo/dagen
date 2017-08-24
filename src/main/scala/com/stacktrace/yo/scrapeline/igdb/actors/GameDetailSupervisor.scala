package com.stacktrace.yo.scrapeline.igdb.actors

import java.util

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailActor.WriteContent
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor.WriteNextObjects
import com.stacktrace.yo.scrapeline.igdb.actors.WriteGameDetailActor.{FinishedWrite, WriteGame}
import com.stacktrace.yo.scrapeline.old.HttpRequestSupervisor.SendNextRequests
import com.stacktrace.yo.scrapeline.old.ScrapeActor.BeginScrape
import org.stacktrace.yo.igdb.model.Game

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

class GameDetailSupervisor(val idSet: Vector[String]) extends Actor with ActorLogging {

  import context.dispatcher

  val idSize: Int = idSet.size
  val maxRetries = 2
  var numVisited = 0
  var inProcess = 0
  val toProcess = scala.collection.mutable.Queue(idSet: _*)

  var writing = 0
  var numWrote = 0
  val toWrite: mutable.Queue[Game] = scala.collection.mutable.Queue[Game]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 2000 millis, self, SendNextRequests())
  val writeTick: Cancellable = context.system.scheduler.schedule(0 millis, 3000 millis, self, WriteNextObjects())

  override def receive: Receive = {
    case SendNextRequests() =>
      if (numVisited < idSet.size) {
        val sendOut = Math.min(toProcess.size, 10 - inProcess)
        for (i <- 1 to sendOut) {
          val url = toProcess.dequeue
          val reader = context.actorOf(Props(new GameDetailActor()))
          reader ! BeginScrape(url)
          inProcess += 1
        }
      }
    case msg@WriteContent(doc: util.List[Game]) => {
      inProcess -= 1
      numVisited += 1
      //      log.info("Recieved Response {} left in queue, {} in process", toProcess.size, inProcess)
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
    }
  }
}

object GameDetailSupervisor {

  case class SendNextRequests()

  case class WriteNextObjects()

}
