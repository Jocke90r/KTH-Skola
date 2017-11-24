package Client.View;

import Client.Net.ServerConnect;

import java.io.IOException;
import java.util.Scanner;

public class Interpreter implements Runnable {

    private final ServerConnect client = new ServerConnect();

    //Startar en tråd för att hantera vyn. Och data från servern som skall skrivas ut till användaren.
    public void start(){
        new Thread(this).start();
    }

    Scanner console = new Scanner(System.in);

   @Override
    public void run(){
        client.connect();

        while(true){
            while(!console.hasNextLine()){
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e){
                    System.out.println("Problem vid läsning av text");
                    e.printStackTrace();
                }
            }
            String input = console.nextLine();
            if(input.toLowerCase().equalsIgnoreCase("quit")){
                break;
            }
            client.messageHandler(input);
        }
       try {
           client.disConnect();
       } catch (IOException e) {
           e.printStackTrace();
       }

   }

    public void writeMessage(String msg){
        System.out.println(msg);
    }
}
