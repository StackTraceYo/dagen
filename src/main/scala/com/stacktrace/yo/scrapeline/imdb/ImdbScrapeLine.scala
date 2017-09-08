package com.stacktrace.yo.scrapeline.imdb

import java.net.URLEncoder

import akka.actor.ActorSystem
import com.stacktrace.yo.scrapeline.engine.core.protocol.EngineProtocol.{EngineMessageType, Scrape}
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeLine
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.ScrapedContent
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element, elementList}

/**
  * Created by Stacktraceyo on 9/6/17.
  */

class ImdbScrapeLine(implicit as: ActorSystem) extends ScrapeLine {

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
        requestAndCall(url, searchIMDB)
      })
    })
  }


  def searchIMDB(doc: ScrapedContent): Unit = {
    val imdburl = "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + URLEncoder.encode(doc.title.split("-")(0).trim, "UTF-8") + "&s=tt"
    requestAndCall(imdburl, printImdbInfo)
  }

  def printImdbInfo(doc: ScrapedContent): Unit = {
    val link = doc >> element("#main div div.findSection table tbody tr:nth-child(1) td.result_text  a")
    val imdbDetail = "http://www.imdb.com" + link.attr("href")
    requestAndCall(imdbDetail, scrapeMovieDetail)
  }

  def scrapeMovieDetail(doc: ScrapedContent): Unit = {
    println(doc.title)
  }


}

object ImdbScrapeLine extends App {

  implicit val as = ActorSystem("imdb")

  val imdbScrapeLine = new ImdbScrapeLine()
  imdbScrapeLine.begin()


}
