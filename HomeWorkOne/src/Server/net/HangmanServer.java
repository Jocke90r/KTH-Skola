package Server.net;

/**
 * Created by Joakim on 2017-11-09.
 */

import Server.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class HangmanServer {

    Controller controller;
    ServerSocket socket;
    ArrayList<Controller> clients = new ArrayList<Controller>();

    //startar servern
    public static void main(String[] args){

        new HangmanServer();
    }

    public HangmanServer(){

        try {
            socket = new ServerSocket(3333); //Listening for new connections in port 3333
            while(true){

                Socket s = socket.accept(); //accepterar en connection om det finns någon.
                controller = new Controller(); //skapar en ny controller som hanterar just denna klient
                controller.CreateHangman(s, this); //skapar ett spel


                //Hangman game = new Hangman(s, this);
                //game.start();
                clients.add(controller); //lägger till användaren
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
