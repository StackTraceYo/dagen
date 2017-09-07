package com.stacktrace.yo.scrapeline.igdb

import java.net.URLEncoder

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element, elementList}
import com.stacktrace.yo.scrapeline.engine.ScrapeLine
import com.stacktrace.yo.scrapeline.engine.core.EngineProtocol.{EngineMessageType, Scrape}
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.ScrapedContent

class IGDBScrapeLine extends ScrapeLine {

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