package se.knowit.levelup.notecrawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CrawlerMessage {
  @JsonProperty("Message")
  private String message;

}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Message {
  private String id;
  private String email;
  private String url;
}