package com.stacktrace.yo.scrapeline.igdb.actors

import java.io.PrintWriter

import akka.actor.{Actor, ActorLogging}
import com.stacktrace.yo.scrapeline.igdb.actors.WriteGameDetailActor.{FinishedWrite, WriteGame}
import org.stacktrace.yo.igdb.model.Game

class WriteGameDetailActor extends Actor with ActorLogging {


  override def receive: Receive = {
    case msg@WriteGame(doc: Game) =>
      val ogSender = sender
      val writer = new PrintWriter("games/" + doc.getName + ".txt")
      writer.write(doc.getUrl)
      writer.flush()
      writer.close()
      ogSender ! FinishedWrite(doc.getName)
  }
  
}

object WriteGameDetailActor {

  case class WriteGame(doc: Game)

  case class FinishedWrite(name: String)

}



