import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        int port = 8989;
        String host = "localhost";
        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println("бизнес");
            String prettyJson;
            do {
                prettyJson = in.readLine();
                System.out.println(prettyJson);
            } while (!prettyJson.endsWith("]"));
        }
    }
}
