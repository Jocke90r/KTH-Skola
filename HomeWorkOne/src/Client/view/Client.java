package Client.view;

/**
 * Created by Joakim on 2017-11-09.
 */



import Client.controller.Controller;


public class Client {                   //Client class creates a controller
                                        //Calls for the method which creates the net socket/connection
                 //And the listen for input from the user
    Controller controller;


    public Client(){

        controller = new Controller();
        controller.start();
        controller.ListenForInput();

    }

}
