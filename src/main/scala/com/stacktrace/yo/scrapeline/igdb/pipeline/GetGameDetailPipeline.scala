package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.io.File

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

    if (new File("games").exists()) {
      println("Games Directory Exists..")
    }
    else if (new File("games").mkdir()) {
      println("Directory was created successfully")
    }
    else {
      println("Failed trying to create the directory")
      as.terminate()
    }

    val supervisor = as.actorOf(Props(new GameDetailSupervisor(idList)))

  }
}
