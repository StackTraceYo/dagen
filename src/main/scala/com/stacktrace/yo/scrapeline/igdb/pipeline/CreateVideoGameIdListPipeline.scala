package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.nio.file.Paths

import akka.NotUsed
import akka.stream.scaladsl.{Broadcast, FileIO, Flow, GraphDSL, Keep, Merge, RunnableGraph, Sink, Source}
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

  def buildAndRun: Unit = {
    //get ids of all themes
//    val themeCountids = (1 to client.themes().count().getCount.toInt).toVector.mkString(",")
//    val genreCountids = (1 to client.genres().count().getCount.toInt).toVector.mkString(",")
//
//    val getThemes = Flow[String].map(getThemesCall)
//    val gameList = Flow[Theme].mapConcat(getGameListFromTheme).async
//
//    val getGenres = Flow[String].map(getGenresCall)
//    val gameListFromGenre = Flow[Genre].mapConcat(getGameListFromGenre).async


//    Source[Theme](getThemesCall(themeCountids))
//      .via(gameList)
//      .map(s => ByteString(s._1 + "," + s._2 + "\n"))
//      .toMat(FileIO.toPath(Paths.get("gamelistfromtheme.txt")))(Keep.right)
//      .run()

//    val g = RunnableGraph.fromGraph(GraphDSL.create() {
//      implicit builder: GraphDSL.Builder[NotUsed] =>
//        import GraphDSL.Implicits._
//
//        val in = Source.single((1 to 50).toVector.mkString(","))
//        in.via(getThemes)
//          .via(gameList)
//          .
//
//        val out = Sink.ignore
//
//        val bcast = builder.add(Broadcast[String](2))
//        val merge = builder.add(Merge[(Int,String)](2))
//
//
//
//        in ~> bcast ~> getThemes
////        bcast ~> getGenres
////        bcast ~> f2 ~> merge ~> f3 ~> out
////        bcast ~> f4 ~> merge
//
//        ClosedShape
//    })


  }


  private def getThemesCall(in: String) = {
    client.themes().withIds(in).go().toVector
  }

  private def getGameListFromTheme(in: Theme) = {
    in.getGames.toList.map(long => (long.toInt, in.getName)).toVector
  }

  private def getGenresCall(in: String) = {
    client.genres().withIds(in).go().toVector
  }

  private def getGameListFromGenre(in: Genre) = {
    in.getGames.toList.map(long => (long.toInt, in.getName)).toVector
  }


  //  def getPipelineSource: Source[jsoup.DocumentType, NotUsed] = Source.single(ScrapeClient.scrape("http://www.the-numbers.com/movie/budgets/all"))
  //
  //  def getParseFlow: Flow[Document, MovieNameAndDetailUrl, NotUsed] = {
  //    Flow[Document]
  //      .mapConcat(doc => {
  //        val table = doc >> elementList("table tr")
  //        val movieLinkTuples = table.flatMap(tr => {
  //          val name = tr >> elementList("tr b a")
  //          name.map(
  //            link => {
  //              MovieNameAndDetailUrl(link.text, "http://www.the-numbers.com/" + link.attr("href"))
  //            }
  //          )
  //        })
  //        movieLinkTuples
  //      })
  //  }
  //
  //  def getPipeOut: Sink[MovieNameAndDetailUrl, Future[IOResult]] = Flow[MovieNameAndDetailUrl]
  //    .map(s => ByteString(s.name + "\n"))
  //    .toMat(FileIO.toPath(Paths.get("movie.txt")))(Keep.right)
  //
  //  def buildAndRun: Future[IOResult] = {
  //    getPipelineSource
  //      .via(getParseFlow)
  //      .runWith(getPipeOut)
  //  }

}
