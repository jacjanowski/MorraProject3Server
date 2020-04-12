import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{

    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    MorraInfo gameInfo;
    int stageCounter = 0;


    Server(Consumer<Serializable> call){

        callback = call;
        server = new TheServer();
        server.start();
        gameInfo = new MorraInfo();
    }


    public class TheServer extends Thread{

        public void run() {

            try(ServerSocket mysocket = new ServerSocket(5555);){
                System.out.println("Server is waiting for a client!");


                while(true) {

                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    c.start();

                    count++;

                    if(clients.size() == 2)
                    {
                        gameInfo.have2Players = true;
                        callback.accept("Two players have joined");
                        callback.accept("The game has begun");
                        //stageCounter++;

                        for(int i = 0; i < 2; i++)
                        {
                            clients.get(i).playerNumber = i+1;
                            //System.out.println("Client #" + i + " is player #" + clients.get(i).playerNumber);
                        }
                    }
                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }


    class ClientThread extends Thread{


        Socket connection;
        int count;
        int playerNumber;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
        }

        public void updateClients(String message) {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(message);
                    t.out.reset();
                }
                catch(Exception e) {}
            }
        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            updateClients("new client on server: client #" + count);

            while(true) {
                try {
                    //only one played has joined the server
                    if(stageCounter == 0)
                    {
                        updateClients("Awaiting another player");
                        stageCounter++;
                    }
                    //Second player has joined the server
                    if(stageCounter == 1)
                    {
                        if (gameInfo.have2Players) {
                            updateClients("Two players have joined");
                            updateClients("Welcome to the ancient game of Morra!");
                            updateClients("Choose a number 0-5");
                            stageCounter++;
                        }

                    }
                    //Game has begun
                    if(stageCounter == 2)
                    {
                        String data = in.readObject().toString();
                        callback.accept("client #" + count + " sent: " + data);
                        updateClients("client #" + count + " said: " + data);
                    }



                }
                catch(Exception e) {
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    updateClients("Client #" + count + " has left the server!");
                    clients.remove(this);
                    break;
                }
            }
        }//end of run


    }//end of client thread
}











