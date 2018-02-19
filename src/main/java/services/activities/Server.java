package services.activities;


import shared.JSONHandler;
import shared.Ports;

public class Server {
    public static void main(String[] args) {

        shared.Server server = new shared.Server("localhost", Ports.ACTIVITIES);

        Controller controller = new Controller();

        server.run(new JSONHandler(controller));
    }
}
