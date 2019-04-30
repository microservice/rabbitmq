package io.storyscript.omg.services.rabbitmq;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.rabbitmq.client.*;
import io.storyscript.omg.services.rabbitmq.entities.BasicPublishPayload;
import io.storyscript.omg.services.rabbitmq.entities.SubscribePayload;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * User: Jude Pereira
 * Date: 2019-04-27
 * Time: 23:22
 */
@RestController
public class WebController {

    private final Connection rabbit;
    private final Channel rabbitPublishOnlyChannel;

    public WebController() throws NoSuchAlgorithmException, KeyManagementException,
            URISyntaxException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(System.getenv("RABBITMQ_URI"));
        rabbit = factory.newConnection();
        rabbitPublishOnlyChannel = rabbit.createChannel();
    }

    @RequestMapping(value = "/basic/publish",
            method = RequestMethod.POST,
            consumes = "application/json")
    public String publishText(@RequestBody BasicPublishPayload payload)
            throws IOException {

        final AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .appId(payload.getAppId())
                .clusterId(payload.getClusterId())
                .contentEncoding(payload.getContentEncoding())
                .contentType(payload.getContentType())
                .expiration(payload.getExpiration())
                .messageId(payload.getMessageId())
                .type(payload.getType())
                .replyTo(payload.getReplyTo())
                .headers(payload.getHeaders())
                .build();

        rabbitPublishOnlyChannel.basicPublish(payload.getExchange(), payload.getRoutingKey(),
                props, payload.getContent().getBytes(StandardCharsets.UTF_8));
        return "ok\n";
    }

    @RequestMapping(
            value = "/subscribe/exchange",
            consumes = "application/json",
            method = RequestMethod.POST)
    public String subscribeToExchange(@RequestBody SubscribePayload payload)
            throws IOException {
        final Channel rabbitChannel = rabbit.createChannel();
        rabbitChannel.exchangeDeclare(payload.getExchange(), payload.getType(), true);
        String queueName = rabbitChannel.queueDeclare().getQueue();
        rabbitChannel.queueBind(queueName, payload.getExchange(), payload.getRoutingKey());
        rabbitChannel.basicConsume(queueName, false, payload.getConsumerTag(),
                new DefaultConsumer(rabbitChannel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        System.out.println("recv from: " + consumerTag + ": " + properties + ": " + new String(body));

                        JSONObject data = new JSONObject()
                                .put("consumer_tag", consumerTag)
                                .put("properties", new JSONObject()
                                        .put("headers", properties.getHeaders())
                                        .put("content_type", properties.getContentType())
                                        .put("content_encoding", properties.getContentEncoding())
                                        .put("type", properties.getType())
                                        .put("message_id", properties.getMessageId())
                                        .put("expiration", properties.getExpiration())
                                        .put("reply_to", properties.getReplyTo())
                                        .put("cluster_id", properties.getClusterId())
                                        .put("app_id", properties.getAppId())
                                )
                                .put("body", new String(body, StandardCharsets.UTF_8));

                        JSONObject ce = new JSONObject()
                                .put("eventType", "trigger")
                                .put("cloudEventsVersion", "0.1")
                                .put("source", payload.getExchange())
                                .put("eventID", UUID.randomUUID().toString())
                                .put("eventTime", new Date().toString())
                                .put("contentType", "application/vnd.omg.object+json")
                                .put("data", data);

                        try {
                            Unirest.post(payload.getEndpoint())
                                    .header("Content-Type", "application/json; charset=utf-8")
                                    .body(ce.toString()).asString();
                        } catch (UnirestException e) {
                            throw new IOException(e);
                        }

                        rabbitChannel.basicAck(envelope.getDeliveryTag(), false);
                    }
                });
        return "ok\n";
    }
}
