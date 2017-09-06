package com.stacktrace.yo.scrapeline.igdb.pipeline.detail.actor

import java.io.PrintWriter
import java.util.UUID

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.stacktrace.yo.scrapeline.core.FileWriting
import com.stacktrace.yo.scrapeline.igdb.pipeline.detail.actor.WriteGameDetailActor.{FinishedWrite, WriteGame}
import org.stacktrace.yo.igdb.model.Game

class WriteGameDetailActor extends Actor with ActorLogging {


  override def receive: Receive = {
    case msg@WriteGame(doc: List[Game]) =>
      val ogSender = sender
      val writer = FileWriting.getJsonWriter
      val names = doc.map(_.getName).mkString(",")
      val pw = new PrintWriter("games/" + UUID.randomUUID().toString + ".json")
      writer.writeValue(pw, doc)
      pw.flush()
      pw.close()
      ogSender ! FinishedWrite(names, doc.size)
      self ! PoisonPill
  }
}

object WriteGameDetailActor {

  case class WriteGame(doc: List[Game])

  case class FinishedWrite(name: String, num: Int)

}



