package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.nio.file.Paths

import akka.stream.scaladsl.{Broadcast, FileIO, Flow, GraphDSL, Keep, Merge, RunnableGraph, Source}
import akka.stream.{ActorMaterializer, ClosedShape, IOResult}
import akka.util.ByteString
import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.IGDBClient
import org.stacktrace.yo.igdb.model.{Genre, Theme}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Stacktraceyo on 8/18/17.
  */
class CreateVideoGameIdListPipeline(implicit val m: ActorMaterializer, implicit val ec: ExecutionContext) {

  import scala.collection.JavaConversions._


  def client: IGDBClient = IGDBAPIClient.getClient

  def buildAndRun: Future[IOResult] = {
    val themeCountids = client.themes().count().getCount.toInt
    val genreCountids = client.genres().count().getCount.toInt

    val getThemes = Flow[String].map(getThemesCall)
    val listFromTheme = Flow[Vector[Theme]].map(getGameListFromThemes).mapConcat(identity)

    val getGenres = Flow[String].map(getGenresCall)
    val listFromGenres = Flow[Vector[Genre]].map(getGameListFromGenres).mapConcat(identity)

    val out = Flow[(Int, String)]
      .map(s => ByteString(s._1 + "," + s._2 + "\n"))
      .toMat(FileIO.toPath(Paths.get("gamelist.txt")))(Keep.right)


    val graph = RunnableGraph.fromGraph(GraphDSL.create(out) {
      implicit builder =>
        sink =>
          import GraphDSL.Implicits._

          val in = Source.single((1 to Math.max(themeCountids, genreCountids)).toVector.mkString(","))
          val bcast = builder.add(Broadcast[String](2))
          val merge = builder.add(Merge[(Int, String)](2))

          in ~> bcast ~> getThemes ~> listFromTheme.mapConcat(identity) ~> merge ~> sink
          bcast ~> getGenres ~> listFromGenres.mapConcat(identity) ~> merge

          ClosedShape
    })
    graph.run()
  }


  private def getThemesCall(in: String): Vector[Theme] = {
    client.themes().withIds(in).go().toVector
  }

  private def getGenresCall(in: String): Vector[Genre] = {
    client.genres().withIds(in).go().toVector
  }

  private def getGameListFromThemes(in: Vector[Theme]) = {
    in.map(getGameListFromTheme)
  }

  private def getGameListFromTheme(in: Theme) = {
    in.getGames.toList.map(long => {
      (long.toInt, in.getName)
    }).toVector
  }

  private def getGameListFromGenres(in: Vector[Genre]) = {
    in.map(getGameListFromGenre)
  }

  private def getGameListFromGenre(in: Genre) = {
    in.getGames.toList.map(long => (long.toInt, in.getName)).toVector
  }

}
