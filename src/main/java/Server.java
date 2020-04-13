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
    int inputCounter = 0;

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
                            //Assign player number to each client thread on server side
                            clients.get(i).playerNumber = i+1;
                            //FIXME add line to send morraInfo containing player number to each client
                            //update server MorraInfo with player number, then send that to client
                            //This allows each client to know which player number they are
                            gameInfo.playerNumber = i + 1;

                            //create temp object that is equal to each client thread
                            ClientThread t = clients.get(i);
                            try {
                                //writes out servers MorraInfo class to each respective client
                                //each one contains a different player number
                                t.out.writeObject("You are Player #" + gameInfo.playerNumber);
                                t.out.reset();
                                t.out.flush();
                                t.out.writeObject(gameInfo);
                                t.out.reset();
                                t.out.flush();
                            }
                            catch(Exception e) {}
                            //print check to make sure each client gets appropriate player number
                            System.out.println("Client #" + i + " is player #" + clients.get(i).playerNumber);
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
                    t.out.flush();
                    gameInfo.playerNumber = i+1;
                    t.out.writeObject(gameInfo);
                    t.out.reset();
                    t.out.flush();
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

            //updateClients("new client on server: client #" + count);

            while(true) {
                try {

                    //only one played has joined the server
                    if(stageCounter == 0)
                    {
                        updateClients("Awaiting another player");
                        stageCounter++;//go to next stage
                    }
                    //Wait for Second player to join the server
                    if(stageCounter == 1)
                    {
                        if (gameInfo.have2Players) {
                            updateClients("Two players have joined");
                            updateClients("Welcome to the ancient game of Morra!");
                            updateClients("Choose a number 0-5");
                            updateClients("Then guess the combined total of you and your opponents numbers");
                            stageCounter++;//go to next stage
                        }
                    }
                    //Game has begun
                    MorraInfo data = (MorraInfo) in.readObject();
                    if(stageCounter == 2) {

                        if(data.playerNumber == 1) {
                            gameInfo.p1Plays = data.p1Plays;
                            gameInfo.p1Guess = data.p1Guess;
                            callback.accept("Player #" + data.playerNumber + " played " + gameInfo.p1Plays);
                            callback.accept("Player #" + data.playerNumber + " guessed " + gameInfo.p1Guess);
                            inputCounter++;
                            //FIXME add this when messages are working
                            //if(inputCounter < 2)
                                //send message to client that server is waiting for other player
                        }
                        if(data.playerNumber == 2) {
                            gameInfo.p2Plays = data.p2Plays;
                            gameInfo.p2Guess = data.p2Guess;
                            callback.accept("Player #" + data.playerNumber + " played " + gameInfo.p2Plays);
                            callback.accept("Player #" + data.playerNumber + " guessed " + gameInfo.p2Guess);
                            inputCounter++;
                            //FIXME add this when messages are working
                            //if(inputCounter < 2)
                                //send message to client that server is waiting for other player
                        }

                        if(inputCounter == 2)
                        {
                            callback.accept("Both players have their input");
                            callback.accept("Moving to play stage of the game");
                            stageCounter++;
                        }

                    }
                    //evaluate which player was awarded points, if any
                    if(stageCounter == 3)
                    {
                        int result = gameInfo.evalPlay();
                        if(result == 0) {
                            updateClients("Both players guessed correct, no points awarded");
                            callback.accept("Both players guessed correct, no points awarded");
                        }
                        if(result == 1)
                        {
                            updateClients("Player 1 is correct, player 1 gets 1 point");
                            callback.accept("Player 1 is correct, gets 1 point!");
                            gameInfo.p1Points++;
                        }
                        if(result == 2)
                        {
                            updateClients("Player 2 is correct, player 2 gets 1 point");
                            callback.accept("Player 2 is correct, gets 1 point!");
                            gameInfo.p2Points++;
                        }
                        if(result == 3) {
                            updateClients("Neither player is correct, no points awarded");
                            callback.accept("Neither player is correct, no points awarded");
                        }

                        //tell player 1 what player 2 played and guessed
                        ClientThread t = clients.get(0);
                        try {
                            t.out.writeObject("Player 2 played " + gameInfo.p2Plays + " and guessed " + gameInfo.p2Guess);
                            t.out.reset();
                            t.out.flush();
                            gameInfo.playerNumber = 1;
                            t.out.writeObject(gameInfo);
                            t.out.reset();
                            t.out.flush();
                        }
                        catch(Exception e) {}

                        //tell player 2 what player 1 played and guessed
                        t = clients.get(1);
                        try {
                            t.out.writeObject("Player 1 played " + gameInfo.p1Plays + " and guessed " + gameInfo.p1Guess);
                            t.out.reset();
                            t.out.flush();
                            gameInfo.playerNumber = 2;
                            t.out.writeObject(gameInfo);
                            t.out.reset();
                            t.out.flush();
                        }
                        catch(Exception e) {}

                        //Display points to server and clients
                        callback.accept("Player 1 has " + gameInfo.p1Points + " points");
                        callback.accept("Player 2 has " + gameInfo.p1Points + " points");
                        callback.accept("Beginning next round");
                        callback.accept("-------------------------");
                        updateClients("Player 1 Points: " + gameInfo.p1Points);
                        updateClients("Player 2 Points: " + gameInfo.p2Points);

                        //display if player 1 has won
                        if(gameInfo.p1Points == 2)
                        {
                            updateClients("!!!!!!!! Player 1 has won with 2 points!");
                        }
                        //display if player 2 has won
                        else if(gameInfo.p2Points == 2)
                        {
                            updateClients("!!!!!!!! Player 2 has won with 2 points!");
                        }
                        //otherwise reset round by going back to 2nd stage and setting input count to 0
                        else {
                            updateClients("Beginning next round");
                            updateClients("-------------------------------");
                            stageCounter = 2;
                            inputCounter = 0;
                        }
                    }

                }
                catch(Exception e) {
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    //updateClients("Client #" + count + " has left the server!");
                    clients.remove(this);
                    updateClients("Awaiting another player");
                    stageCounter = 1;
                    break;
                }
            }
        }//end of run


    }//end of client thread
}











