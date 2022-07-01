import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File srcDir = new File("pdfs");
        System.out.println("Индексирование pdf файлов в папке " + srcDir.getPath());
        var engine = new BooleanSearchEngine(srcDir);
        int port = 8989;
        Server server = new Server(port, engine);

        System.out.println("Запуск сервера на порту: " + port);
        server.start();
    }

}