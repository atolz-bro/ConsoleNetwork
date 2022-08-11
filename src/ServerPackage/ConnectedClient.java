package ServerPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectedClient {
    Socket clientSocket;
    String nameId;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;

    ConnectedClient(Socket clientSocket, String nameId) throws IOException {
        this.clientSocket = clientSocket;
        this.nameId = nameId;
        objIn = new ObjectInputStream(clientSocket.getInputStream());
        objOut =  new ObjectOutputStream(clientSocket.getOutputStream());
        //Why exactly do we need to flush for this to work properly
        objOut.flush();
    }

}
