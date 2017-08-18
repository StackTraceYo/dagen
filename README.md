# scrapeline

An akka stream based application and in the future a library to help build web scraping/api pipelines

This is also a nice simple use case of Akka-Streams

inspired by [scrapy](https://github.com/scrapy/scrapy) and after reading [Predict Movie Rating](https://blog.nycdatascience.com/student-works/machine-learning/movie-rating-prediction)

 To build this out  I am following the procedures he used in his github repo mentioned in his article. (scraping movie data)

##imdb scraper

1. ImdbScraper
    * This is the intro point into the application it calls the flows in order
2. The first pipeline is MovieListPipeline
    * This scrapes [the numbers](http://www.the-numbers.com/") to get the top 5000+ movie names and writes them out to a txt file (movies.txt)
    * This App waits for this flow to finish before proceeding
3. The second pipeline "MovieNameToIMDBPipeline" reads the names in the file and
    * Generates a search query url against imdb
    * This url is then scraped to get the first result returned (IMDBSearchSubPipe)
    * This new url is then passed into the IMDBDetailSubPipe to extract the information from the page
        * The next step is to scrape this actual page into a "MovieDetail" (In progress)