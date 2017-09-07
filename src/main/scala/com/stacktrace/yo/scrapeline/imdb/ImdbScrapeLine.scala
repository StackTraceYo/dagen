package com.stacktrace.yo.scrapeline.imdb

import com.stacktrace.yo.scrapeline.engine.BaseScrapeLine
import com.stacktrace.yo.scrapeline.engine.core.EngineProtocol.{EngineMessageType, Scrape}
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.ScrapedContent
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

/**
  * Created by Stacktraceyo on 9/6/17.
  */

class ImdbScrapeLine extends BaseScrapeLine {

  override def start: List[EngineMessageType] = {

    val pages = for (i <- 101 to 5500 by 100) yield {
      Scrape("http://www.the-numbers.com/movie/budgets/all/" + i)
    }

    //    List(Scrape("http://www.the-numbers.com/movie/budgets/all")) ::: pages.toList
    List(Scrape("http://www.the-numbers.com/movie/budgets/all"))
  }


  override def beginScrape(doc: ScrapedContent): Unit = {

    val table = doc >> elementList("table tr")
    var i = 0
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


  def parseEachMovie(doc: ScrapedContent): Unit = {
    println(doc.toString)
  }


}

object ImdbScrapeLine extends App {

  val imdbScrapeLine = new ImdbScrapeLine()
  imdbScrapeLine.begin()


}
