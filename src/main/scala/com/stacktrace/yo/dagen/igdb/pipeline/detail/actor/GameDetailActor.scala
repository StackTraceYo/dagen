package com.stacktrace.yo.dagen.igdb.pipeline.detail.actor

import java.util

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.stacktrace.yo.dagen.igdb.pipeline.detail.actor.GameDetailActor.{GetIds, WriteContent}
import com.stacktrace.yo.dagen.old.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.IGDBClient
import org.stacktrace.yo.igdb.client.game.GameFields
import org.stacktrace.yo.igdb.model.Game

class GameDetailActor extends Actor with ActorLogging {

  def client: IGDBClient = IGDBAPIClient.getClient

  override def receive: Receive = {
    case msg@GetIds(ids: String) =>
      val oSender = sender
      log.info("Getting {}", ids)
      val doc = client.games().withIds(ids).withFields(GameFields.ALL).go()
      sender ! WriteContent(doc)
      log.info("Response Returned .. Closing")
      self ! PoisonPill
  }
}

object GameDetailActor {

  case class GetIds(ids: String)

  case class WriteContent(doc: util.List[Game])

}



