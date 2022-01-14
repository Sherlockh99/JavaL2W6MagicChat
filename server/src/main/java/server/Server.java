package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private static final int PORT = 8189;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            socket = server.accept();
            System.out.println("Client connected!");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while (true){
                String str = in.readUTF();
                if (str.equals("/end")){
                    break;
                }
                out.writeUTF("ECHO: " + str);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            System.out.println("Client disconnect!");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        };
    }
}
