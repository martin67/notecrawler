package se.knowit.levelup.notecrawler;


import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.knowit.levelup.notecrawler.domain.NoteCrawlerDocument;
import se.knowit.levelup.notecrawler.domain.NoteCrawlerRepository;

public class BasicCrawler extends WebCrawler {

    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

    private final AtomicInteger numSeenImages;

    private final NoteCrawlerRepository noteCrawlerRepository;

    /**
     * Creates a new crawler instance.
     *
     * @param numSeenImages This is just an example to demonstrate how you can pass objects to crawlers. In this
     *                      example, we pass an AtomicInteger to all crawlers and they increment it whenever they see a url which points
     *                      to an image.
     */
    public BasicCrawler(AtomicInteger numSeenImages, NoteCrawlerRepository noteCrawlerRepository) {
        this.numSeenImages = numSeenImages;
        this.noteCrawlerRepository = noteCrawlerRepository;
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            numSeenImages.incrementAndGet();
            return false;
        }

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        return href.startsWith("https://michaelkravchuk.com/");
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Domain: '{}'", domain);
        logger.debug("Sub-domain: '{}'", subDomain);
        logger.debug("Path: '{}'", path);
        logger.debug("Parent page: {}", parentUrl);
        logger.debug("Anchor text: {}", anchor);


        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            links.stream().filter(link -> link.getURL().endsWith(".pdf"))
                    .forEach(weburl -> {
                        NoteCrawlerDocument noteCrawlerDocument = new NoteCrawlerDocument();
                        noteCrawlerDocument.setFreeText(text);
                        noteCrawlerDocument.setPdfLink(weburl.getURL());
                        noteCrawlerRepository.save(noteCrawlerDocument);
                    });

            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
            logger.debug("Number of outgoing links: {}", links.size());
        }

        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
            logger.debug("Response headers:");
            for (Header header : responseHeaders) {
                logger.debug("\t{}: {}", header.getName(), header.getValue());
            }
        }

        logger.debug("=============");
    }
}