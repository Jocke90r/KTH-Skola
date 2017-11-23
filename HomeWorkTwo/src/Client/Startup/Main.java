package Client.Startup;


import Client.controller.Controller;
import Client.Net.ServerConnect;
import Client.View.Interpreter;

/**
 * Created by Chosrat on 2017-11-21.
 */
public class Main {

    public static void main(String[] args){
        Interpreter game = new Interpreter();
        game.start();
    }

}
