package controller;

/**
 * Created by Chosrat on 2017-11-09.
 */
import java.net.Socket;

import model.Hangman;
import net.HangmanServer;





public class Controller {

    public void CreateHangman(Socket socket, HangmanServer server){ //

        Hangman hangMan = new Hangman(socket, server);
        hangMan.start();
    }

}
