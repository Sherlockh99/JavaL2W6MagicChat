package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private static final int PORT = 8189;
    private static Scanner sc;
    private static PrintWriter out;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            socket = server.accept();
            System.out.println("Client connected!");

            sc = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true); //сразу отправляет

            while (true){
                String str = sc.nextLine();
                if (str.equals("/end")){
                    break;
                }
                System.out.println("Client: " + str);
                out.println("ECHO: " + str);
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
