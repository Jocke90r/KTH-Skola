package Client.View;


import Client.Net.ServerConnect;

import java.util.Scanner;

public class Interpreter implements Runnable{
    Scanner console = new Scanner(System.in);
    ServerConnect client = new ServerConnect();

    public void start() {
        new Thread(this).start();
    }
    @Override
    public void run() {
        client.connect();
        while(true){
            while(!console.hasNextLine()){
                try{
                    Thread.sleep(1);
                }catch (InterruptedException e){
                    System.out.println("problem vid läsning av text");
                    e.printStackTrace();
                }
            }
            String input = console.nextLine();
                    if(input.toLowerCase().equals("quit")) {
                        break;
                    }
                    client.messageHandler(input);
    }
    }
    }
