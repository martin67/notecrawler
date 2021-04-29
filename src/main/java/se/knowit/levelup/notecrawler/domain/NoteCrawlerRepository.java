package se.knowit.levelup.notecrawler.domain;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteCrawlerRepository extends SolrCrudRepository <NoteCrawlerDocument, String> {

}
