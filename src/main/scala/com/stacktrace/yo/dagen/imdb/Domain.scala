package com.stacktrace.yo.dagen.imdb

object Domain {

  case class MovieNameAndDetailUrl(name: String, url: String)

  case class MovieNameAndImdbUrl(name: String, url: String)


  case class MovieItem(
                        movieName : String,
                        movieImdbLink: String,
                        imdbScore: String,
                        movieTitle: String,
                        titleYear: String,
                        numVotedUsers: String,
                        genres: String,
                        budget: String,
                        color: String,
                        gross: String,
                        duration: String,
                        country: String,
                        language: String,
                        plotKeywords: String,
                        storyline: String,
                        aspectRatio: String,
                        contentRating: String,
                        numUserForReviews: String,
                        numCriticForReviews: String,
                        castInfo: String,
                        directorInfo: String,
                        numFacebookLikes: String,
                        imageUrls: String,
                        images: String
                      )

}
