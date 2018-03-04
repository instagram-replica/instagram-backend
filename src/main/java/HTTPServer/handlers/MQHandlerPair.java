package HTTPServer.handlers;

import shared.MQServer.Queue;

public class MQHandlerPair {
    public String uuid;
    public Queue queue;

    public MQHandlerPair(String uuid, Queue queue) {
        this.uuid = uuid;
        this.queue = queue;
    }
}
