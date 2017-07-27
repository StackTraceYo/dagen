package com.stacktrace.yo.scrapeline.imdb.pipelines

import java.nio.file.Paths

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import com.stacktrace.yo.scrapeline.core.ScrapeClient.jsoup
import com.stacktrace.yo.scrapeline.core._
import com.stacktrace.yo.scrapeline.imdb.Domain.MovieNameAndDetailUrl
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

import scala.concurrent.Future

class MovieListPipeline(implicit val m: ActorMaterializer) {

  def getPipelineSource: Source[jsoup.DocumentType, NotUsed] = Source.single(ScrapeClient.scrape("http://www.the-numbers.com/movie/budgets/all"))

  def getParseFlow: Flow[Document, MovieNameAndDetailUrl, NotUsed] = {
    Flow[Document]
      .mapConcat(doc => {
        val table = doc >> elementList("table tr")
        val movieLinkTuples = table.flatMap(tr => {
          val name = tr >> elementList("tr b a")
          name.map(
            link => {
              MovieNameAndDetailUrl(link.text, "http://www.the-numbers.com/" + link.attr("href"))
            }
          )
        })
        movieLinkTuples
      })
  }

  def getPipeOut: Sink[MovieNameAndDetailUrl, Future[IOResult]] = Flow[MovieNameAndDetailUrl]
    .map(s => ByteString(s.name + "\n"))
    .toMat(FileIO.toPath(Paths.get("movie.txt")))(Keep.right)

  def buildAndRun: Future[IOResult] = {
    getPipelineSource
      .via(getParseFlow)
      .runWith(getPipeOut)
  }

}
