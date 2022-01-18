package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private static final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public Server(){

        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");

            while (true){
               socket = server.accept();
               System.out.println("Client connected: " + socket.getRemoteSocketAddress());
               //subscribe(new ClientHandler(this,socket));
                new ClientHandler(this,socket);
            }

        }catch(IOException e){
            e.printStackTrace();
        }finally {
            System.out.println("Server stop");
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }

    public void broadcastMsg(ClientHandler sender, String msg){
        String message = String.format("[ %s ]: %s", sender.getNickName(),msg);
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandler sender, String nickName, String msg){
        String message = String.format("[ %s ]: %s", sender.getNickName(),msg);
        for (ClientHandler client : clients) {
            if(nickName.equals(client.getNickName()) || (sender==client)){
                client.sendMsg(message);
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }
}
