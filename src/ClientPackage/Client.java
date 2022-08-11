package ClientPackage;
import MessageModel.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static int serverPort = 9999;;
    static String serverIp = "localhost" ;
    static Socket connectingSocket;
    static ObjectInputStream inMessage;
    static String name = "";
    static String password = "";

    public static void main(String[] args) throws Exception {
        connect();
        login();
        listenToServer();
        sendToServer();
    }

    static void login() throws IOException {
        Scanner serverScanner = new Scanner(connectingSocket.getInputStream());
        boolean success = false;
        System.out.println("Welcome to console network,Do you want to Register or Login");

        while (!success) {
            PrintWriter firstMessage = new PrintWriter(connectingSocket.getOutputStream(),true);
            Scanner sc = new Scanner(System.in);
            String option = sc.nextLine();
            if(option.equals("login")){
                System.out.println("Enter Your Username and password seperated by space");
                name = sc.next();
                password = sc.next();
                firstMessage.println("login "+name+" "+password);
                String serverResponse = serverScanner.nextLine();
                success = serverResponse.equals("success");
                if(!success){
                    System.out.println("server response: "+serverResponse);
                    System.out.println("Do you want to Register or Login");
                }
            }else {
                System.out.println("Enter Your desired Username and password seperated by space");
                name = sc.next();
                password = sc.next();
                firstMessage.println("register "+name+" "+password);
                firstMessage.flush();
                //System.out.println("flushed");
                String serverResponse = serverScanner.nextLine();
                success = serverResponse.equals("success");
                if(!success){
                    System.out.println("server response: "+serverResponse);
                    System.out.println("Do you want to Register or Login");
                }
            }

        }
    }

    static void connect() throws IOException {
        connectingSocket = new Socket(serverIp,serverPort);
        //System.out.println("client port: " + connectingSocket.getLocalPort());
        //System.out.println("Out of connect()");
    }

    static void listenToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inMessage = new ObjectInputStream(connectingSocket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true){
                    try {
                        //System.out.println("in listening...");
                        Message message = (Message) inMessage.readObject();
                        System.out.println(message.getFromName()+": "+message.getMessage());
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Server Stopped");
                        break;

                    }
                }
            }
        }).start();
    }

    static void sendToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Scanner scan = new Scanner(System.in);
                    ObjectOutputStream outMessage = new ObjectOutputStream(connectingSocket.getOutputStream());
                    outMessage.flush();
                    String message;
                    String toName;
                    Message netmessage;
                    System.out.println("Your Chat Console, Enter your message");
                    while (true) {
                        message = scan.nextLine();
                        System.out.println("To: ");
                        toName = scan.nextLine();
                        netmessage = new Message(message, name, toName);
                        outMessage.writeObject(netmessage);
                    }
                } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        }).start();
    }
}
