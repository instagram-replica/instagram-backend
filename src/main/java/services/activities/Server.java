package services.activities;


import java.io.IOException;

public class Server {
    public static void main(String[] args) {
        try {
            Controller controller = new Controller();
            shared.MQServer.Server server = new shared.MQServer.Server("activities");

            server.run(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
