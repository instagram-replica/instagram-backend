package services.chats;


import shared.JSONHandler;
import shared.Ports;

public class Server {
    public static void main(String[] args) {

        shared.Server server = new shared.Server("localhost", Ports.CHATS);

        Controller controller = new Controller();

        server.run(new JSONHandler(controller));

    }
}
