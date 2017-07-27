package com.stacktrace.yo.scrapeline.imdb.pipelines

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.stacktrace.yo.scrapeline.core.ScrapeClient
import com.stacktrace.yo.scrapeline.core.ScrapeClient.jsoup
import com.stacktrace.yo.scrapeline.imdb.Domain.{MovieItem, MovieNameAndImdbUrl}

import scala.concurrent.{ExecutionContext, Future}


class IMDBDetailSubPipe(implicit val m: ActorMaterializer, implicit val ec: ExecutionContext) extends {

  def getMovieDetails: Flow[MovieNameAndImdbUrl, MovieItem, NotUsed] = {
    Flow[MovieNameAndImdbUrl]
      .mapAsyncUnordered(30)(getDocumentFromUrl)
      .mapAsyncUnordered(20)(parseMovieDetail)
  }


  private def parseMovieDetail(in: (String, jsoup.DocumentType)): Future[MovieItem] = Future {
    val document = in._2
    //todo parse document here
    null
  }

  private def getDocumentFromUrl(in: MovieNameAndImdbUrl): Future[(String, jsoup.DocumentType)] = Future {
    (in.name, ScrapeClient.scrape(in.url))
  }

}
