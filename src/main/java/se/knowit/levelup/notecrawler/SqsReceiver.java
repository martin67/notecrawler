package se.knowit.levelup.notecrawler;

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
  void test() {
    log.info("Starting test schedule");
    SqsClient sqsClient = SqsClient.create();
    ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
        .queueUrl("https://sqs.eu-north-1.amazonaws.com/838808947613/newUrlQueue")
        .maxNumberOfMessages(5)
        .build();
    List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

    for (Message message : messages) {
      log.info("Setting seed {}", message.body());
      basicCrawlController.setSeed(message.body());
      sqsClient.deleteMessage(DeleteMessageRequest.builder().receiptHandle(message
              .receiptHandle()).queueUrl("https://sqs.eu-north-1.amazonaws.com/838808947613/newUrlQueue").build());
    }

    if (!messages.isEmpty()) {
      log.info("Starting crawl controller!");
      basicCrawlController.start();
    }
  }

}
