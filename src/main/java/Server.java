import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private final int port;
    private final BooleanSearchEngine engine;

    public Server(int port, BooleanSearchEngine engine) {
        this.port = port;
        this.engine = engine;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream())) {
                    String word = in.readLine();
                    var pageEntryList = engine.search(word);

                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();

                    String jsonPageEntryList = gson.toJson(pageEntryList,
                            new TypeToken<List<PageEntry>>() {}.getType());

                    out.println(jsonPageEntryList);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
            e.printStackTrace();
        }
    }
}
