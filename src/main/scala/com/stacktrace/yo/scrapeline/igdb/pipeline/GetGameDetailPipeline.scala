package com.stacktrace.yo.scrapeline.igdb.pipeline

import akka.actor.{ActorSystem, Props}
import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor
import org.stacktrace.yo.igdb.client.IGDBClient

import scala.io.Source

/**
  * Created by Ahmad on 8/23/2017.
  */
class GetGameDetailPipeline(implicit as: ActorSystem) {

  def client: IGDBClient = IGDBAPIClient.getClient

  def run(): Unit = {

    val idList = Source.fromFile("gamecombined.txt")
      .getLines()
      .toVector
      .map(each => {
        each.split(",")(0)
      })

    val supervisor = as.actorOf(Props(new GameDetailSupervisor(idList)))


  }

  //  def run(): Unit = {
  //    Source.fromFile("gamecombined.txt")
  //      .getLines()
  //      .foreach(line => {
  //        val tokens = line.split(",")
  //        val id = tokens(0)
  //        val genre = tokens(1)
  //        gameMap.get(id) match {
  //          case None =>
  //            gameMap.put(id, genre)
  //          case Some(v) =>
  //            gameMap.put(id, v + "," + genre)
  //        }
  //      })
  //  }

}
