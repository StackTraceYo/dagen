package com.stacktrace.yo.scrapeline.imdb.pipelines

import java.net.URLEncoder
import java.nio.file.Paths

import akka.stream.scaladsl.{FileIO, Flow, Framing, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import akka.{Done, NotUsed}
import com.stacktrace.yo.scrapeline.imdb.Domain.MovieNameAndDetailUrl

import scala.concurrent.{ExecutionContext, Future}

class MovieNameToIMDBPipeline(implicit val m: ActorMaterializer, implicit val ec: ExecutionContext) {

  private def getPipelineSource: Source[String, Future[IOResult]] = {
    FileIO.fromPath(Paths.get("movie.txt"))
      .via(Framing.delimiter(ByteString("\n"), 256)
        .map(_.utf8String)
      )
  }

  private def getDetailUrlFlow: Flow[String, MovieNameAndDetailUrl, NotUsed] = {
    Flow[String]
      .mapAsyncUnordered(100)(mapPipeToImdbSearch)
  }


  private def mapPipeToImdbSearch(in: String): Future[MovieNameAndDetailUrl] = Future {
    val encodedString: String = URLEncoder.encode(in, "UTF-8")
    MovieNameAndDetailUrl(in, "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + encodedString + "&s=tt")
  }


  def getOutput: Future[Done] = {
    getPipelineSource
      .via(getDetailUrlFlow)
      .via(new IMDBSearchSubPipe().getSubFlow)
      .runForeach(x => println(x.name + ":" + x.url))
  }

}
