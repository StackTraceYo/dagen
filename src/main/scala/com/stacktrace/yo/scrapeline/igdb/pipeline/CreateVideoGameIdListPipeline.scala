package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.io.{BufferedWriter, File, FileWriter}

import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.IGDBClient
import org.stacktrace.yo.igdb.model.{Genre, Theme}

/**
  * Created by Stacktraceyo on 8/18/17.
  */
class CreateVideoGameIdListPipeline {

  import scala.collection.JavaConversions._


  def client: IGDBClient = IGDBAPIClient.getClient

  def run(): Unit = {
    val themeCountids = client.themes().count().getCount.toInt
    val genreCountids = client.genres().count().getCount.toInt
    val ids = (1 to Math.max(themeCountids, genreCountids)).toVector.mkString(",")


    val file = new File("gametheme.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    getThemesCall(ids)
      .flatMap(getGameListFromTheme)
      .foreach(gametheme => {
        bw.write(gametheme._1 + "," + gametheme._2 + "\n")
      })
    bw.close()

    val file2 = new File("gamegenre.txt")
    val bw2 = new BufferedWriter(new FileWriter(file2))
    getGenresCall(ids)
      .flatMap(getGameListFromGenre)
      .foreach(gamegenre => {
        bw2.write(gamegenre._1 + "," + gamegenre._2 + "\n")
      })
    bw2.close()

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
