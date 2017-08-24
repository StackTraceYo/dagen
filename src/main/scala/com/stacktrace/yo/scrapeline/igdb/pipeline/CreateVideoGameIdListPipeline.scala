package com.stacktrace.yo.scrapeline.igdb.pipeline

import java.io.{BufferedWriter, File, FileWriter}

import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import org.stacktrace.yo.igdb.client.IGDBClient
import org.stacktrace.yo.igdb.model.{Genre, Theme}

import scala.io.Source

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

    val gameMap = collection.mutable.HashMap[String, String]()

    Source.fromFile("gamegenre.txt")
      .getLines()
      .foreach(line => {
        val tokens = line.split(",")
        val id = tokens(0)
        val genre = tokens(1)
        gameMap.get(id) match {
          case None =>
            gameMap.put(id, genre)
          case Some(v) =>
            gameMap.put(id, v + "/" + genre)
        }
      })

    Source.fromFile("gametheme.txt")
      .getLines()
      .foreach(line => {
        val tokens = line.split(",")
        val id = tokens(0)
        val genre = tokens(1)
        gameMap.get(id) match {
          case None =>
            gameMap.put(id, genre)
          case Some(v) =>
            gameMap.put(id, v + "/" + genre)
        }
      })

    val finalFile = new File("gamecombined.txt")
    val finalBw = new BufferedWriter(new FileWriter(finalFile))
    gameMap
      .toList
      .sortWith(sortById)
      .foreach(game => {
        finalBw.write(game._1 + "," + game._2 + "\n")
      })
    finalBw.close()
    println("Finished Video Game Name List Flow..")
    println("Found " + gameMap.size + " games")
  }

  def sortById(s1: (String, String), s2: (String, String)): Boolean = {
    s1._1.toLong < s2._1.toLong
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
