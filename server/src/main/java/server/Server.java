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
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public void broadcastMsg(ClientHandler sender, String msg){
        String message = String.format("[ %s ]: %s", sender.getNickName(),msg);
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg){
        String message = String.format("[ %s ] to [ %s ] : %s", sender.getNickName(), receiver, msg);
        for (ClientHandler client : clients) {
            //if(receiver.equals(client.getNickName()) || (sender==client)){
            if(receiver.equals(client.getNickName())){
                client.sendMsg(message);
                if(!sender.getNickName().equals(receiver)){
                    sender.sendMsg(message);
                }
                return;
            }
        }
        sender.sendMsg("not found user: " + receiver);
    }

    public boolean isLoginAuthenticated(String login){
        for(ClientHandler c: clients){
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public void broadcastClientList(){
        StringBuilder sb = new StringBuilder("/clientList");
        for (ClientHandler client : clients) {
            sb.append(" ").append(client.getNickName());
        }

        String message = sb.toString();

        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }

    }

    public AuthService getAuthService() {
        return authService;
    }
}
