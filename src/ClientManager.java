import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    public String name;
    public static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket){
        try{
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = in.readLine();
            clients.add(this);
            broadcastMessage("Server: " + name + " подключился к чату.");
        } catch (IOException e){
            closeEverythung(socket, in, out);
        }
    }

    private void broadcastMessage(String msgToSent) {
        for(ClientManager client: clients){
            try{
                if(!client.name.equals(name)){
                    client.out.write(msgToSent);
                    client.out.newLine();
                    client.out.flush();
                }
            } catch (IOException e) {
                closeEverythung(socket, in, out);
            }
        }
    }

    private void closeEverythung(Socket socket, BufferedReader in, BufferedWriter out) {
        removeClient();
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clients.remove(this);
        broadcastMessage("Server: " + name + " покинул чат!");
    }

    @Override
    public void run() {
        String msgFromClient;

        while(socket.isConnected()){
            try{
                msgFromClient = in.readLine();
                broadcastMessage(msgFromClient);
            } catch (IOException e){
                closeEverythung(socket, in, out);
                break;
            }
        }
    }
}