package com.stacktrace.yo.scrapeline.igdb.pipeline.list

import java.io.{BufferedWriter, File, FileWriter}

import akka.actor.PoisonPill
import com.stacktrace.yo.scrapeline.core.IGDBAPIClient
import com.stacktrace.yo.scrapeline.core.Protocol.{PhaseFinished, StartPhase}
import com.stacktrace.yo.scrapeline.core.pipeline.PipelineActor
import org.stacktrace.yo.igdb.client.IGDBClient
import org.stacktrace.yo.igdb.model.{Genre, Theme}

import scala.io.Source

/**
  * Created by Stacktraceyo on 8/18/17.
  */
class GameListPipeline extends PipelineActor {

  import scala.collection.JavaConversions._


  def client: IGDBClient = IGDBAPIClient.getClient


  override def receive: Receive = {
    case StartPhase() =>
      if (!canSkip) {
        val themeCountids = client.themes().count().getCount.toInt
        val genreCountids = client.genres().count().getCount.toInt
        val ids = (1 to Math.max(themeCountids, genreCountids)).toVector.mkString(",")

        val file = new File("gamegenretheme.txt")
        val bw = new BufferedWriter(new FileWriter(file))
        getThemesCall(ids)
          .flatMap(getGameListFromTheme)
          .foreach(gametheme => {
            bw.write(gametheme._1 + "," + gametheme._2 + "\n")
          })
        getGenresCall(ids)
          .flatMap(getGameListFromGenre)
          .foreach(gamegenre => {
            bw.write(gamegenre._1 + "," + gamegenre._2 + "\n")
          })

        bw.flush()
        bw.close()
        log.info("Completed GameGenreTheme Id Collection")
      }


      val gameMap = collection.mutable.HashMap[String, String]()

      Source.fromFile("gamegenretheme.txt")
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
      log.info("Combining Completed")
      val finalFile = new File("gamecombined.txt")
      val finalBw = new BufferedWriter(new FileWriter(finalFile))
      gameMap
        .toList
        .sortWith(sortById)
        .foreach(game => {
          finalBw.write(game._1 + "," + game._2 + "\n")
        })
      finalBw.flush()
      finalBw.close()
      log.info("Finished Video Game Name List Flow..")
      log.info("Found {} game ids", gameMap.size)
      sender() ! PhaseFinished("GameListPipeline")
      self ! PoisonPill
  }


  override def canSkip: Boolean = {
    if (new File("gamegenretheme.txt").exists()) {
      log.info("GameGenreTheme Id File Exists.. Skipping Collection")
      true
    }
    false
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
