package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.io.File

import akka.actor.{ActorRef, Props}
import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import com.stacktrace.yo.scrapeline.core.pipeline.PipelineActor
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor
import com.stacktrace.yo.scrapeline.igdb.actors.GameDetailSupervisor.StartSupervisor
import com.stacktrace.yo.scrapeline.igdb.pipeline.IGDBPipelineController.{Finished, PhaseFinished, StartPhase}
import org.stacktrace.yo.igdb.client.IGDBClient

import scala.io.Source

/**
  * Created by Ahmad on 8/23/2017.
  */
class GetGameDetailPipeline(controller: ActorRef) extends PipelineActor {

  def client: IGDBClient = IGDBAPIClient.getClient


  override def receive: PartialFunction[Any, Unit] = {
    case StartPhase() =>
      val supervisor = context.actorOf(Props(new GameDetailSupervisor(start())))
      supervisor ! StartSupervisor(self)
    case Finished() =>
      controller ! PhaseFinished("GetGameDetailPipeline")
  }

  private def start(): Vector[String] = {

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
    }
    idList
  }
}
