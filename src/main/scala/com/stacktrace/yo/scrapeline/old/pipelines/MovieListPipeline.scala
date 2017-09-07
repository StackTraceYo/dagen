package com.stacktrace.yo.scrapeline.old.pipelines

import java.io.{BufferedWriter, File, FileWriter}

import com.stacktrace.yo.scrapeline.imdb.Domain.MovieNameAndDetailUrl
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

class MovieListPipeline {

  def run(): Unit = {

    val scraper = JsoupBrowser()
    val doc = scraper.get("http://www.the-numbers.com/movie/budgets/all")
    val table = doc >> elementList("table tr")
    val movieLinkTuples = table.flatMap(tr => {
      val name = tr >> elementList("tr b a")
      name.map(
        link => {
          MovieNameAndDetailUrl(link.text, "http://www.the-numbers.com/" + link.attr("href"))
        }
      )
    })
    val file = new File("movie.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    movieLinkTuples.foreach(tuple => {
      bw.write(tuple.name + "\n")
    })
    bw.close()
  }
}

