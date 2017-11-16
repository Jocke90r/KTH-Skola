package Server.controller;

/**
 * Created by Chosrat on 2017-11-09.
 */
import java.net.Socket;

import Server.model.Hangman;
import Server.net.HangmanServer;





public class Controller {

    public void CreateHangman(Socket socket, HangmanServer server){ //

        Hangman hangMan = new Hangman(socket, server);
        hangMan.start();
    }

}
