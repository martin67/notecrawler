package se.knowit.levelup.notecrawler;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public class SqsReceiver {

  @Autowired
  private BasicCrawlController basicCrawlController;

  @Scheduled(fixedDelay = 10)
  void test() {
    SqsClient sqsClient = SqsClient.create();
    ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
        .queueUrl("https://sqs.eu-north-1.amazonaws.com/838808947613/newUrlQueue")
        .maxNumberOfMessages(5)
        .build();
    List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

    for (Message message : messages) {
      basicCrawlController.setSeed(message.body());
    }
    basicCrawlController.start();
  }

}
