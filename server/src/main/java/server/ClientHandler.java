package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authenticated;
    private String nickName;
    private String login;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {

                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            sendMsg("/end");
                            break;
                        }
                        if(str.startsWith("/auth")) {
                            String[] token = str.split(" ",3);
                            if(token.length<3){
                                continue;
                            }
                            String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1],token[2]);
                            login = token[1];
                            if(newNick!=null){
                                if(!server.isLoginAuthenticated(login)){
                                    authenticated = true;
                                    nickName = newNick;
                                    sendMsg("/authok " + nickName);
                                    server.subscribe(this);
                                    System.out.println("Client: " + nickName + " authenticated");
                                    break;
                                } else{
                                    sendMsg("Под этим логином уже зашли в чат");
                                }
                            }else {
                                sendMsg("Неверный логин / пароль");
                            }
                        }
                    }

                    //цикл работы
                    while (authenticated) {
                        String str = in.readUTF();
                        if(str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                break;
                            }
                            if(str.startsWith("/w")) {
                                String[] token = str.split(" ",3);
                                if(token.length<3){
                                    continue;
                                }
                                server.privateMsg(this,token[1],token[2]);
                            }
                        }else {
                            server.broadcastMsg(this, str);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    System.out.println("Client disconnect!");
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickName() {
        return nickName;
    }

    public String getLogin() {
        return login;
    }
}
