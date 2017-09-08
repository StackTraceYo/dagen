package com.stacktrace.yo.scrapeline.igdb

import akka.actor.ActorSystem
import com.stacktrace.yo.scrapeline.engine.core.protocol.EngineProtocol.{EngineMessageType, Request}
import com.stacktrace.yo.scrapeline.engine.http.HttpLine
import com.stacktrace.yo.scrapeline.old.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.game.GameFields

class IGDBScrapeLine(implicit as: ActorSystem) extends HttpLine {

  override def beginRead(doc: String): Unit = {
    println(doc)
  }

  override def start: List[EngineMessageType] = {
    List(Request(IGDBAPIClient.getClient.games().withFields(GameFields.ALL).create()))
  }
}

object IGDBScrapeLine extends App {
  implicit val as = ActorSystem("igdb")

  val igdbScrapeline = new IGDBScrapeLine()
  igdbScrapeline.begin()
}