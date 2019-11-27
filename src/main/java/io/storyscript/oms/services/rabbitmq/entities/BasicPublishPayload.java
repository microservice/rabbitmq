package io.storyscript.oms.services.rabbitmq.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * User: Jude Pereira
 * Date: 2019-04-30
 * Time: 11:45
 */
public class BasicPublishPayload {
    private String exchange;

    @JsonProperty("routing_key")
    private String routingKey;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("cluster_id")
    private String clusterId;

    @JsonProperty("content_encoding")
    private String contentEncoding;

    @JsonProperty("content_type")
    private String contentType;

    private String expiration;

    @JsonProperty("message_id")
    private String messageId;

    private String type;

    @JsonProperty("reply_to")
    private String replyTo;

    @JsonProperty("headers")
    private Map<String, Object> headers;

    private String content;

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getAppId() {
        return appId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getType() {
        return type;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getContent() {
        return content;
    }
}
