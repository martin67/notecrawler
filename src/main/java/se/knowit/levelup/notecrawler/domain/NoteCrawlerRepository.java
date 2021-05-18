package se.knowit.levelup.notecrawler.domain;

import org.springframework.data.solr.repository.SolrCrudRepository;

public interface NoteCrawlerRepository extends SolrCrudRepository<NoteCrawlerDocument, String> {
}
