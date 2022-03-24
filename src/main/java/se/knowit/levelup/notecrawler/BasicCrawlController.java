package se.knowit.levelup.notecrawler;

import java.util.concurrent.atomic.AtomicInteger;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.knowit.levelup.notecrawler.domain.NoteCrawlerRepository;

@Service
public class BasicCrawlController {
    final NoteCrawlerRepository noteCrawlerRepository;

    CrawlConfig crawlConfig = new CrawlConfig();
    CrawlController controller;
    CrawlController.WebCrawlerFactory<BasicCrawler> factory;

    // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
    // speed depends on many other factors as well. You can experiment with this to figure out what number of
    // threads works best for you.
    int numberOfCrawlers = 8;

    public BasicCrawlController(NoteCrawlerRepository noteCrawlerRepository) throws Exception {

        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        crawlConfig.setCrawlStorageFolder("/tmp/crawler4j/");

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        crawlConfig.setPolitenessDelay(1000);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        crawlConfig.setMaxDepthOfCrawling(-1);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        crawlConfig.setMaxPagesToFetch(1000);

        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        crawlConfig.setIncludeBinaryContentInCrawling(true);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        crawlConfig.setResumableCrawling(true);

        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.
        //config.setHaltOnError(true);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);


        // To demonstrate an example of how you can pass objects to crawlers, we use an AtomicInteger that crawlers
        // increment whenever they see a url which points to an image.
        AtomicInteger numSeenImages = new AtomicInteger();

        // The factory which creates instances of crawlers.
        factory = () -> new BasicCrawler(numSeenImages, noteCrawlerRepository);

        this.noteCrawlerRepository = noteCrawlerRepository;
    }

    public void setSeed(String seedUrl) {
        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        //https://michaelkravchuk.com/free-sheet-music/
        controller.addSeed(seedUrl);
    }

    public void start() {
        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);
    }

    public void shutdown() {
        controller.shutdown();
    }
}