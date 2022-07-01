import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File srcDir = new File("pdfs");
        System.out.println("Индексирование pdf файлов в папке " + srcDir.getPath());
        var engine = new BooleanSearchEngine(srcDir);
        System.out.println("Запуск сервера");
        startServer(engine);
    }

    private static void startServer(BooleanSearchEngine engine) {
        try (ServerSocket serverSocket = new ServerSocket(8989)) {
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