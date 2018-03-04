package shared.MQServer;

public class Queue {
    private String name;

    public Queue(String name) {
        this.name = name;
    }

    public String getRequestQueueName() {
        return this.name + "_req";
    }

    public String getResponseQueueName() {
        return this.name + "_res";
    }
}
