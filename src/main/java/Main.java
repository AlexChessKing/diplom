import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8989)) { // стартуем сервер один(!) раз
            System.out.println("Сервер запущен!");
            BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

            while (true) { // в цикле(!) принимаем подключения
                try (Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                    ) {
                    String request = in.readLine();
                    String answer = engine.search(request).toString();
                    out.println(answer);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
