import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String name;

    public ChatClient(Socket socket, String userName) {
        this.socket = socket;
        name = userName;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverythung(socket, in, out);
        }
    }

    public void sentMessage() {
        try {
            out.write(name);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                out.write(name + ": " + message);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeEverythung(socket, in, out);
        }
    }

    public void listenFromMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroup;
                while(socket.isConnected()) {
                    try{
                        msgFromGroup = in.readLine();
                        System.out.println(msgFromGroup);
                    } catch (IOException e) {
                        closeEverythung(socket, in, out);
                    }
                }
            }
        }).start();
    }

    private void closeEverythung(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите свое имя: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket("localhost", 8888);
        ChatClient chatClient = new ChatClient(socket, userName);
        chatClient.listenFromMessage();
        chatClient.sentMessage();
    }
}