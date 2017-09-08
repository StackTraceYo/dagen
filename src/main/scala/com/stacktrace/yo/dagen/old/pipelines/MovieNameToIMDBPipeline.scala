package com.stacktrace.yo.dagen.old.pipelines

import java.net.URLEncoder

import com.stacktrace.yo.dagen.engine.scrape.ScrapeProtocol.ScrapedContent
import com.stacktrace.yo.dagen.imdb.Domain.{MovieNameAndDetailUrl, MovieNameAndImdbUrl}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element

import scala.io.Source

class MovieNameToIMDBPipeline {

  val jsoup = JsoupBrowser()

  def run(): Iterator[(String, ScrapedContent)] = {
    Source.fromFile("movie.txt")
      .getLines()
      .map(line => {
        val encodedString: String = URLEncoder.encode(line, "UTF-8")
        MovieNameAndDetailUrl(line, "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + encodedString + "&s=tt")
      })
      .map(nameDetail => {
        val document = jsoup.get(nameDetail.url)
        val link = document >> element("#main div div.findSection table tbody tr:nth-child(1) td.result_text  a")
        MovieNameAndImdbUrl(nameDetail.name, "http://www.imdb.com" + link.attr("href"))
      })
      .map(nameImdb => {
        (nameImdb.name, jsoup.get(nameImdb.url))
      })
  }
}
