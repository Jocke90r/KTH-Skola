package view;

/**
 * Created by Chosrat on 2017-11-09.
 */


import client.ClientConnection;
import controller.Controller;

/**
 * Created by Chosrat on 2017-11-04.
 */
public class Client {                   //Client class creates a controller
    //Calls for the method which creates the client socket/connection
    ClientConnection cc;                //And the listen for input from the user
    Controller controller;


    public Client(){

        controller = new Controller();
        controller.start();
        controller.ListenForInput();

    }

}
