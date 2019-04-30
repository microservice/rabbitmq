package io.storyscript.omg.services.rabbitmq;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.BasicDBObject;
import com.rabbitmq.client.*;
import io.storyscript.omg.services.rabbitmq.entities.BasicPublishPayload;
import io.storyscript.omg.services.rabbitmq.entities.SubscribePayload;
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
import java.util.HashMap;
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

                        final HashMap<String, Object> headers = new HashMap<>();
                        properties.getHeaders().forEach((key, value) -> {
                            if (value instanceof LongString) {
                                value = value.toString();
                            }
                            headers.put(key, value);
                        });

                        BasicDBObject data = new BasicDBObject()
                                .append("consumer_tag", consumerTag)
                                .append("properties", new BasicDBObject()
                                        .append("headers", headers)
                                        .append("content_type", properties.getContentType())
                                        .append("content_encoding", properties.getContentEncoding())
                                        .append("type", properties.getType())
                                        .append("message_id", properties.getMessageId())
                                        .append("expiration", properties.getExpiration())
                                        .append("reply_to", properties.getReplyTo())
                                        .append("cluster_id", properties.getClusterId())
                                        .append("app_id", properties.getAppId())
                                )
                                .append("body", new String(body, StandardCharsets.UTF_8));

                        BasicDBObject ce = new BasicDBObject()
                                .append("eventType", "trigger")
                                .append("cloudEventsVersion", "0.1")
                                .append("source", payload.getExchange())
                                .append("eventID", UUID.randomUUID().toString())
                                .append("eventTime", new Date().toString())
                                .append("contentType", "application/vnd.omg.object+json")
                                .append("data", data);

                        try {
                            Unirest.post(payload.getEndpoint())
                                    .header("Content-Type", "application/json; charset=utf-8")
                                    .body(ce.toJson()).asString();
                        } catch (UnirestException e) {
                            throw new IOException(e);
                        }

                        rabbitChannel.basicAck(envelope.getDeliveryTag(), false);
                    }
                });
        return "ok\n";
    }
}
