package my.examples.miniwebserver;

public class Application {
    public static void main(String[] args) {
        WebServer server = new WebServer(9000);

        server.run();
    }
}
