package http_server.websocket;

import com.rabbitmq.client.Channel;

public class Pair {
    private Channel channel;
    private String subscriptionTag;

    public Pair(Channel x, String y) {
        this.channel = x;
        this.subscriptionTag = y;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getSubscriptionTag() {
        return subscriptionTag;
    }
}
