package com.stacktrace.yo.scrapeline.igdb.pipeline

import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.IGDBClient
import org.stacktrace.yo.igdb.model.{Genre, Theme}

/**
  * Created by Stacktraceyo on 8/18/17.
  */
class CreateVideoGameIdListPipeline {

  import scala.collection.JavaConversions._


  def client: IGDBClient = IGDBAPIClient.getClient

  def buildAndRun(): Unit = {
    val themeCountids = client.themes().count().getCount.toInt
    val genreCountids = client.genres().count().getCount.toInt
    val ids = (1 to Math.max(themeCountids, genreCountids)).toVector.mkString(",")

    getThemesCall(ids)
      .flatMap(getGameListFromTheme)
    getGenresCall(ids)
      .flatMap(getGameListFromGenre)
  }


  private def getThemesCall(in: String): Vector[Theme] = {
    client.themes().withIds(in).go().toVector
  }

  private def getGenresCall(in: String): Vector[Genre] = {
    client.genres().withIds(in).go().toVector
  }


  private def getGameListFromTheme(in: Theme) = {
    in.getGames.toList.map(long => {
      (long.toInt, in.getName)
    }).toVector
  }

  private def getGameListFromGenre(in: Genre) = {
    in.getGames.toList.map(long => (long.toInt, in.getName)).toVector
  }

}
