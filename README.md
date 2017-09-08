# Data Gathering Engine (dagen )
Data collection engine I am building to collect/gather data from web pages, apis, or other data sources. 

- The base "engine" for collecting the data is based off of workflow of when using [Scrapy](https://scrapy.org/)
    * It is built with [Akka](http://akka.io/) in scala

- IMDB portion inspired after reading [Predict Movie Rating](https://blog.nycdatascience.com/student-works/machine-learning/movie-rating-prediction)
 
- To collect video game, I am using my library [java-igdb](https://github.com/stacktraceyo/java-igdb) to call the [igdb-api](https://www.igdb.com/api) 
 
 
## imdb scraper

1. Currently protyping new engine implementation with imdb
   * see imdb folder
## igdb collector

####  * uses old prototype engine *
 
 this currently builds an actor system and passes messages up and downstream like so:

1. IgdbScraper
    * This is the intro point into the application it calls the flows in order
2. The first pipeline is CreateVideoGameIdList
    * This scrapes [igdb-api](https://www.igdb.com/api) using using my library [java-igdb](https://github.com/stacktraceyo/java-igdb)
    * It first gets all the game id with a genre or theme by querying for the game lists by the genres
    * Once all the ids are gotten that have genres/themes, all the ids are collected and then in batches of 1000 they are requested and stored in a json file with random uuid name. 
    * the next step is to extract the data we want from the 30k+ movie objects written out.
        * the games.zip is a zip file with all the collected json from whenver it last ran.
        Unpack the zip to get the collected json of all the games queried for.
    
   
## book collector
* Not Started
* Need to find an api for books
    
    