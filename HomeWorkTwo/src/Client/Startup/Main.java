package Client.Startup;


import Client.Net.ServerConnect;
import Client.View.Interpreter;

/**
 * Created by Joakim on 2017-11-21.
 */
public class Main {

    public static void main(String[] args){
      Interpreter interpreter= new Interpreter();
      interpreter.start();
    }

}


