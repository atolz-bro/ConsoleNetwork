# ConsoleNetwork
A project that allows computers to connect and send messages through a central server

ConsoleNetwork is a project that demonstrates a network connection between multiple client computers connected through a central server.
The network is built with Java Sockets.<br>
A Socket is a computer technology that allows computers to transfer data when connected through some means(e.g Wi-Fi/Hotspot,ethernet) using a protocol that determines how and in what structure the data(binary data) gets transfered

## Main Project Classes and Library

The project is made up of a Server class,a Client class, a Message class, a ConnectedClient class and a Postgres JDBC Driver jar.

A Servers instance makes a connection a postgres database to manage multiple clients computers connection and messages; it allows clients to register or login when connected to server, so they can send data to other clients.The Server also helps to save incoming messages for clients not currently on the server.

A Client instance allows a computer to get connected to the network and communicate with other clinets; multiple instances connect to a single instance of the server.

A Message instance contains the message string that clients instances use to send and recieve data/messages to other clients via the server;

A ConnectedClient instance is used by the server to manage clients that are active on the server

The postgres JDBC Driver jar(Library) is what allows the JDBC API in the JDK ```java.sql``` package, used by the server instance, to connect to the postgres database.
The path to this library has to be set through the environment variable or done in an IDE like intelliJ Idea

https://user-images.githubusercontent.com/62163687/184237759-aa4c4969-d0f0-43b0-b4cc-f5089ff710d5.mp4

