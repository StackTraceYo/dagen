package com.stacktrace.yo.dagen.igdb.pipeline.extract.actor

import akka.actor.{Actor, ActorLogging}
import com.stacktrace.yo.dagen.igdb.pipeline.extract.actor.GameDataExtractionDelegate.Process
import org.stacktrace.yo.igdb.model.Game

/**
  * Created by Ahmad on 8/24/2017.
  */
class GameDataExtractionActor extends Actor with ActorLogging {

  import scala.collection.JavaConversions._


  override def receive: Receive = {
    case Process(game: Game) =>
      //(Field Name, Keep , idField , Value)
      val gameFields = List(
        ("franchise", true, true, game.getFranchise),
        ("keywords", true, true, game.getKeywords),
        ("developers", true, true, game.getDevelopers),
        ("rating", true, false, game.getRating),
        ("created_at", true, false, game.getCreatedAt),
        ("videos", false, true, game.getVideos),
        ("aggregated_rating_count", true, false, game.getAggregatedRatingCount),
        ("alternative_names", true, false, game.getAlternativeNames),
        ("time_to_beat", false, false, game.getTimeToBeat),
        ("player_perspectives", false, true, game.getPlayerPerspectives),
        ("screenshots", false, false, game.getScreenshots),
        ("cover", false, false, game.getCover),
        ("themes", false, true, game.getThemes),
        ("updated_at", false, true, game.getUpdatedAt),
        ("pulse_count", false, true, game.getPulseCount),
        ("genres", false, true, game.getGenres),
        ("first_release_date", true, true, game.getFirstReleaseDate),
        ("storyline", true, false, game.getStoryline),
        ("popularity", true, true, game.getPopularity),
        ("dlcs", true, true, game.getDlcs),
        ("release_dates", false, false, game.getReleaseDates),
        ("games", true, true, game.getGames),
        ("publishers", true, true, game.getPublishers),
        ("total_rating", true, false, game.getTotalRating),
        ("id", true, false, game.getId),
        ("expansions", true, true, game.getExpansions),
        ("slug", true, true, game.getSlug),
        ("hypes", false, false, game.getHypes),
        ("summary", true, true, game.getSummary),
        ("pegi", true, false, game.getPegi),
        ("game_modes", false, true, game.getGameModes),
        ("weighted_rating", true, true, game.getWeightedRating),
        ("collection", true, true, game.getCollection),
        ("url", false, false, game.getUrl),
        ("tags", true, true, game.getTags),
        ("rating_count", true, false, game.getRatingCount),
        ("esrb", true, false, game.getEsrb),
        ("name", true, false, game.getName),
        ("total_rating_count", true, false, game.getTotalRatingCount),
        ("aggregated_rating", true, false, game.getAggregatedRating),
        ("game_engines", false, true, game.getGameEngines),
        ("websites", false, false, game.getWebsites),
        ("category", true, true, game.getCategory),
        ("status", false, true, game.getStatus),
        ("multiplayer_modes", false, false, game.getMultiplayerModes),
        ("standalone_expansions", true, true, game.getStandaloneExpansions),
        ("game", true, true, game.getGame))

      val keepFields =
        gameFields
          .filter(fields => fields._2 && fields._3)
          .map(all => {
            (all._1, all._4)
          })
          .map(tuple => {
            tuple._2 match {
              case s: java.util.List[Long] =>
                (tuple._1, s.toList)
              case l: java.lang.Long =>
                (tuple._1, List(l.toLong))
              case _ =>
                (tuple._1, List(-9.toLong))
            }
          })

      keepFields.foreach(println)

  }
}

object GameDataExtractionActor {

  case class ProcessedGame(finished: FinishedFields, idFields: IdFields)

  case class FinishedFields(fields: List[(String, String)])

  case class IdFields(fields: List[(String, Long)])

}

