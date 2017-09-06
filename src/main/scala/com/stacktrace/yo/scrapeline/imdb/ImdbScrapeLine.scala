package com.stacktrace.yo.scrapeline.imdb

import akka.actor.ActorSystem
import com.stacktrace.yo.scrapeline.core.ScrapeClient.jsoup
import com.stacktrace.yo.scrapeline.engine.{BaseScrapeLine, Scrapeline}
import com.stacktrace.yo.scrapeline.engine.core.EngineProtocol.{Begin, EngineMessageType, Scrape}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by Stacktraceyo on 9/6/17.
  */

class ImdbScrapeLine extends BaseScrapeLine {

  override def start: List[EngineMessageType] = List(Scrape("http://www.the-numbers.com/movie/budgets/all"))


  override def scrape(doc: jsoup.DocumentType): Unit = {

    val table = doc >> elementList("table tr")
    table.foreach(tr => {
      val name = tr >> elementList("tr b a")
      name.map(
        link => {
          "http://www.the-numbers.com/" + link.attr("href")
        }
      ).foreach(url => {
        andThenScrape(url, parseEachMovie)
      })
    })
  }


  def parseEachMovie(doc: jsoup.DocumentType): Unit = {
    println(doc.toString)
  }


}

object ImdbScrapeLine extends App {

  val imdbScrapeLine = new ImdbScrapeLine()
  imdbScrapeLine.begin()


}
