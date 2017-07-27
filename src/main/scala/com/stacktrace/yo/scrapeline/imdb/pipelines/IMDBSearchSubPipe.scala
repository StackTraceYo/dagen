package com.stacktrace.yo.scrapeline.imdb.pipelines

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.stacktrace.yo.scrapeline.core.ScrapeClient
import com.stacktrace.yo.scrapeline.imdb.Domain.{MovieNameAndDetailUrl, MovieNameAndImdbUrl}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._

import scala.concurrent.{ExecutionContext, Future}


class IMDBSearchSubPipe(implicit val m: ActorMaterializer, implicit val ec: ExecutionContext) extends {

  def getSubFlow: Flow[MovieNameAndDetailUrl, MovieNameAndImdbUrl, NotUsed] = {
    Flow[MovieNameAndDetailUrl]
      .mapAsyncUnordered(100)(mapSearchUrlToFoundUrl)
  }

  private def mapSearchUrlToFoundUrl(in: MovieNameAndDetailUrl): Future[MovieNameAndImdbUrl] = Future {
    val document = ScrapeClient.scrape(in.url)
    val link = document >> element("#main div div.findSection table tbody tr:nth-child(1) td.result_text  a")
    MovieNameAndImdbUrl(in.name, "http://www.imdb.com" + link.attr("href"))
  }

}
