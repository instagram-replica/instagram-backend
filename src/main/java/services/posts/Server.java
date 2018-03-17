package services.posts;



public class Server {
    public static void main(String[] args) {

            Controller controller = new Controller();

            shared.MQServer.Server server = new shared.MQServer.Server("posts");

            server.run(controller);


    }
}
