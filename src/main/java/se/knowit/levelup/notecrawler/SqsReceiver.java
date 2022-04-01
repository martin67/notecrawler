package se.knowit.levelup.notecrawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class SqsReceiver {

  @Autowired
  private BasicCrawlController basicCrawlController;

  public SqsReceiver(BasicCrawlController basicCrawlController) {
    this.basicCrawlController = basicCrawlController;
  }

  @PostConstruct
  void startCrawler(){

  }

  @Scheduled(fixedDelay = 5000)
  void test() throws JsonProcessingException {
    log.info("Starting test schedule");
    SqsClient sqsClient = SqsClient.create();
    ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
        .queueUrl("https://sqs.eu-north-1.amazonaws.com/838808947613/newUrlQueue")
        .maxNumberOfMessages(5)
        .build();
    List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

    for (Message message : messages) {
      String body = message.body();
      log.info("Message body {}",message.body());
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      CrawlerMessage crawlerMessage = objectMapper.readValue(body, CrawlerMessage.class);
      String jsonMessage = crawlerMessage.getMessage();

      se.knowit.levelup.notecrawler.Message message1 = objectMapper.readValue(jsonMessage, se.knowit.levelup.notecrawler.Message.class);
      log.info("Message received {}", message1);
      log.info("Setting seed {}", message1.getUrl());
      basicCrawlController.setSeed(message1.getUrl());
      sqsClient.deleteMessage(DeleteMessageRequest.builder().receiptHandle(message
              .receiptHandle()).queueUrl("https://sqs.eu-north-1.amazonaws.com/838808947613/newUrlQueue").build());
    }

    if (!messages.isEmpty()) {
      log.info("Starting crawl controller!");
      basicCrawlController.start();
    }
  }

}
