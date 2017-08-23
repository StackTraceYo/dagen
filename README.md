# scrapeline

Data collection for a meta prediction engine

IMDB portion inspired after reading [Predict Movie Rating](https://blog.nycdatascience.com/student-works/machine-learning/movie-rating-prediction)
 
To collect video game, I am using my library [java-igdb](https://github.com/stacktraceyo/java-igdb) to call the [igdb-api](https://www.igdb.com/api) 
 
 

##imdb scraper

1. ImdbScraper
    * This is the intro point into the application it calls the flows in order
2. The first pipeline is MovieListPipeline
    * This scrapes [the numbers](http://www.the-numbers.com/") to get the top 5000+ movie names and writes them out to a txt file (movies.txt)
3. The second pipeline "MovieNameToIMDBPipeline" reads the names in the file and
    * Generates a search query url against imdb
    * This url is then scraped to get the first result returned (IMDBSearchSubPipe)
    * This new url is then passed into the IMDBDetailSubPipe to extract the information from the page
        * The next step is to scrape this actual page into a "MovieDetail" (In progress)
        
##igdb collector

1. IgdbScraper
    * This is the intro point into the application it calls the flows in order
2. The first pipeline is CreateVideoGameIdList
    * This scrapes [igdb-api](https://www.igdb.com/api) using using my library [java-igdb](https://github.com/stacktraceyo/java-igdb)
    * for now it first gets all the game id with a genre or theme by querying for the game lists by the genres
   
##book collector
* Not Started
* Need to find an api for books
    
    