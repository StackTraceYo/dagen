package com.stacktrace.yo.scrapeline.igdb.actors

import java.io.PrintWriter

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.stacktrace.yo.scrapeline.core.FileWriting
import com.stacktrace.yo.scrapeline.igdb.actors.WriteGameDetailActor.{FinishedWrite, WriteGame}
import org.stacktrace.yo.igdb.model.Game

class WriteGameDetailActor extends Actor with ActorLogging {


  override def receive: Receive = {
    case msg@WriteGame(doc: Game) =>
      val ogSender = sender
      val writer = FileWriting.getJsonWriter
      val pw = new PrintWriter("games/" + doc.getId + ".json")
      writer.writeValue(pw, doc)
      pw.flush(); pw.close()
      ogSender ! FinishedWrite(doc.getName)
      self ! PoisonPill
  }
}

object WriteGameDetailActor {

  case class WriteGame(doc: Game)

  case class FinishedWrite(name: String)

}



