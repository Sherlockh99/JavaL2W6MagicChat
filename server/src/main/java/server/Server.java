package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private static final int PORT = 8189;

    public Server(){
        try (ServerSocket serverSocket = server = new ServerSocket(PORT)) {
            System.out.println("Server started!");

            while (true){
               socket = server.accept();
               System.out.println("Client connected: " + socket.getRemoteSocketAddress());
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
}
