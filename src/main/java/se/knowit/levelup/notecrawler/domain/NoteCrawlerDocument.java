package se.knowit.levelup.notecrawler.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

//@SolrDocument(collection = "notecrawler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteCrawlerDocument {
    @Id
//    @Indexed(name = "pdfLink", type = "string", required = true)
    private String pdfLink;

//    @Indexed(name = "composer", type = "text_general")
    private String composer;

//    @Indexed(name = "title", type = "text_general")
    private String title;

//    @Indexed(name = "instrument", type = "text_general")
    private String instrument;

//    @Indexed(name = "key", type = "text_general")
    private String key;

//    @Indexed(name = "freeText", type = "text_general")
    private String freeText;
}
