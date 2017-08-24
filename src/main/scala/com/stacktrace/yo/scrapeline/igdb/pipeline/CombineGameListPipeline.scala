package com.stacktrace.yo.scrapeline.igdb.pipeline

import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.IGDBClient

import scala.io.Source

/**
  * Created by Stacktraceyo on 8/18/17.
  */
class CombineGameListPipeline {


  def client: IGDBClient = IGDBAPIClient.getClient

  def run(): Unit = {

    val gameMap = collection.mutable.HashMap[String,String]()

    Source.fromFile("gamegenre.txt")
      .getLines()
      .map(line => {
        val tokens = line.split(",")
        val id = tokens(0)
        val genre = tokens(1)
        gameMap.get(id) match {
          case None =>
          case Some(v) =>
        }
      })

  }
}
