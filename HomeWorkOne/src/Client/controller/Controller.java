package Client.controller;

/**
 * Created by Joakim on 2017-11-09.
 */

import Client.net.ClientConnection;

public class Controller {

    private final ClientConnection cc = new ClientConnection();     //Controller creates a private connection to the server

    public void start(){                                            //Starts the connection to the server
        cc.start();
    }

    public void ListenForInput(){                                   //Calls the method, listenforinput
        cc.listenForInput();
    }


}
