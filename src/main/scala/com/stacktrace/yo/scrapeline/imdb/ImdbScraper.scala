package com.stacktrace.yo.scrapeline.imdb

import com.stacktrace.yo.scrapeline.imdb.pipelines.{MovieListPipeline, MovieNameToIMDBPipeline}

import scala.language.postfixOps

object ImdbScraper extends App {

  val movieNameList = new MovieListPipeline().run()
  val movieNameToImdbDocument = new MovieNameToIMDBPipeline().run()


}
