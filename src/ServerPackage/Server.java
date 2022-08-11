package ServerPackage;

import MessageModel.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    static private final String url = "jdbc:postgresql://localhost:5432/consolenetworkserverdb";
    static private final String user = "postgres";
    static private final String password = "YOUR DATABASE PASSWORD";

    //Clients online to receive Messages
    static ArrayList<ConnectedClient> connectedClients = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server Started...");

        while (!serverSocket.isClosed()){
            Socket clientSocket = serverSocket.accept();
            System.out.println("new connection...");

            //A new thread to handle each connection to the server to avoid blocking
            new Thread(() -> {
                String endPoint = "endpoint";
                String clientName = "client";
                String clientPassword = "password";
                try {
                    boolean successful = false;
                    Scanner scan = new Scanner(clientSocket.getInputStream());
                    PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),true);

                    while (!successful){
                        endPoint = scan.next();
                        clientName = scan.next();
                        clientPassword = scan.next();

                        if(endPoint.equals("login")){
                            //Login
                            successful = login(clientName,clientPassword);
                            if(successful){
                                System.out.println(clientName+ " logged in");
                                //Feedback to Client
                                pw.println("success");

                            }else{
                                //Feedback to Client
                                pw.println("invalid user name and login");
                            }
                        }else {
                           //Register
                           successful = register(clientName,clientPassword);
                            if(successful){
                                System.out.println(clientName+ " registered");
                                pw.println("success");
                            }else{
                                //Feedback to Client
                                pw.println("Username already chosen");
                            }
                        }
                    }
                    //Class to handle connected sockets and identify them by clientName
                    ConnectedClient client = new ConnectedClient(clientSocket,clientName);
                    connectedClients.add(client);

                    //Check if this client has a message while offline
                    checkInbox(client);

                    //blocks
                    while (!client.clientSocket.isClosed()){
                        //Listen to message from this client
                        Message message = receiveMessage(client);
                        System.out.println(message);
                        send(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println(clientName+": disconnected");
                }
            }).start();
        }

    }

    static boolean login(String name, String pass){
        boolean successful = false;
        String sql_login = "select * from RegisteredUser where name = '"+name + "' and password = '"+pass+"'";
        try (
                Connection con = DriverManager.getConnection(url,user,password);
                Statement statement = con.createStatement() )
        {
            ResultSet rs = statement.executeQuery(sql_login);
            if(rs.next()){
                successful = true;
            }
        }catch (SQLException e){
            System.out.println("sql login: "+e.getMessage());
        }
        return successful;
    }

    static boolean register(String name, String pass){
        boolean successful = false;
        String sql_register = "insert into RegisteredUser (name,password) values(?, ?)";
        try (
                Connection con = DriverManager.getConnection(url,user,password);
                PreparedStatement statement = con.prepareStatement(sql_register))
        {
            statement.setString(1,name);
            statement.setString(2,pass);
            int count = statement.executeUpdate();
            if(count == 1){
                successful = true;
            }
        }catch (SQLException e){
            System.out.println("sql register: "+e.getMessage());
        }
        return successful;
    }

    static void checkInbox(ConnectedClient client) {
        String sql_checkInbox = "SELECT message, fromName, toName FROM Message WHERE toName = ?";

        try (
                Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = con.prepareStatement(sql_checkInbox)) {
            statement.setString(1,client.nameId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                String message = rs.getString(1);
                String fromName = rs.getString(2);
                String toName = rs.getString(3);
                Message inbox = new Message(message,fromName,toName);
                send(inbox);
                clearInbox(toName);
            }

        } catch (SQLException | IOException e) {
            System.out.println("sql check: " + e.getMessage());
        }

    }

    static void clearInbox(String toName){
        String sql_checkInbox = "DELETE FROM Message WHERE toName = ?";

        try (
                Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = con.prepareStatement(sql_checkInbox)) {
            statement.setString(1,toName);
            int rs = statement.executeUpdate();
            System.out.println("clearInbox(executeUpdate): "+rs);

        } catch (SQLException e) {
            System.out.println("sql check: " + e.getMessage());
        }
    }

    static boolean saveToInbox(Message message){
        boolean successful = false;
        String sql_register = "insert into Message (message,fromName,toName) values(?, ?,?)";
        try (
                Connection con = DriverManager.getConnection(url,user,password);
                PreparedStatement statement = con.prepareStatement(sql_register))
        {
            statement.setString(1,message.getMessage());
            statement.setString(2, message.getFromName());
            statement.setString(3, message.getToName());
            int count = statement.executeUpdate();
            if(count == 1){
                successful = true;
            }
        }catch (SQLException e){
            System.out.println("sql saveToInbox: "+e.getMessage());
        }
        return successful;
    }

    static Message receiveMessage(ConnectedClient client) throws IOException, ClassNotFoundException {
        ObjectInputStream inMessage = client.objIn;
        Message message = (Message) inMessage.readObject();
        System.out.println("receive successful from"+ client.nameId);
        return message;
    }

    /*Receives toMessage from a client thread, converts the ToMessage
    to a FromMessage and Server sends it to the client having the to-name in the ToMessage
    * */
    static void send(Message message) throws IOException {
        boolean online = false;
        //System.out.println("in send "+message.message);
        for(ConnectedClient client : connectedClients){
            if(client.nameId.equals(message.toName)){
                online=true;
                ObjectOutputStream outMessage = client.objOut;
                outMessage.writeObject(message);
                System.out.println("sent successful to: "+client.nameId);
            }
        }
        if(!online)
            if(saveToInbox(message))
                System.out.println("Saved to db: "+message);
    }


}
