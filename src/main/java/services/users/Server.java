package services.users;

public class Server {
    public static void main(String[] args) {
        Controller controller = new Controller();
        shared.MQServer.Server server = new shared.MQServer.Server("users");
        server.run(controller);
    }
}
