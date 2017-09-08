package com.stacktrace.yo.dagen.igdb

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import com.stacktrace.yo.dagen.engine.core.protocol.EngineProtocol.{CallHttp, EngineMessageType}
import com.stacktrace.yo.dagen.engine.http.HttpLine
import com.stacktrace.yo.dagen.old.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.game.GameFields

class IGDBScrapeLine(implicit as: ActorSystem) extends HttpLine {

  override def beginRead(doc: String): Unit = {
    println(doc)
  }

  override def start: List[EngineMessageType] = {

    val igdbHeaders: scala.collection.immutable.Seq[HttpHeader] = scala.collection.immutable.Seq(
      RawHeader("user-key", IGDBAPIClient.getClient.getApiKey),
      RawHeader("Accept", "application/json")
    )

    List(CallHttp(
      HttpRequest(uri = IGDBAPIClient.getClient.games().withFields(GameFields.ALL).create(), headers = igdbHeaders)
    ))
  }
}

object IGDBScrapeLine extends App {
  implicit val as = ActorSystem("igdb")

  val igdbScrapeline = new IGDBScrapeLine()
  igdbScrapeline.begin()
}