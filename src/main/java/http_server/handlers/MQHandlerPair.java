package http_server.handlers;

public class MQHandlerPair {
    public String uuid;
    public String serviceName;

    public MQHandlerPair(String uuid, String serviceName) {
        this.uuid = uuid;
        this.serviceName = serviceName;
    }
}
