package Server.model;

/**
 * Created by Joakim on 2017-11-09.
 */

import Server.net.HangmanServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.random;


public class Hangman extends Thread{

    Socket socket;
    HangmanServer server;
    DataInputStream dataIn;
    DataOutputStream dataOut;

    public ArrayList<String> words;
    private int guesses;
    private int scoreBoard;
    public String gameWord;
    public char[] splitGameWord;
    public char[] playResult;
    private int step = 0;
    boolean hitOrMiss;
    private  String guess;

    public Hangman(Socket socket, HangmanServer server){
        super("HangManThread");
        this.socket = socket;
        this.server = server;
        this.guesses = 0;
    }

    public void run(){

        try {
            dataIn = new DataInputStream(socket.getInputStream()); //input från klienten
            dataOut = new DataOutputStream(socket.getOutputStream());//output till klienten (console)
            dataOut.writeUTF("Welcome to Hangman game, to play input 'yes' or to exit input 'quit' ");
            dataOut.flush();

            while (true) { //så länge det inte finns data så ska vi söva tråden en millisekund för att inte ta CPUkraft hela tiden
                while (dataIn.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String input = dataIn.readUTF();
                if (input.equalsIgnoreCase("yes")) { //starta spelet om klienter skickar "yes"
                    startGame();
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startGame(){
        hangManWord();                              //skapar en lista med ord
        gameWord = randomWord();                    //hämtar ett ord från listan som innehåller alla ord
        splitGameWord = gameWord.toCharArray();     //delar upp ordet till en array av chars för att kunna jämföra.
        playResult = new char[splitGameWord.length];//skapar en chararray med så många tecken som spelordet är på.
        guesses = splitGameWord.length;             //sätter antalet gissningar till längden på ordet
        System.out.println(gameWord);               //skapar CHEEEATS om man har tillgång till servern

        try {
            dataOut.writeUTF("Start game: \nYou have " + splitGameWord.length + " guesses and the word has " + splitGameWord.length + " letters " +
                    "\nGuess one letter or the entire word: \nYour score is: " + scoreBoard );
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {//om det finns något i dataIn så sätts guess till det ordet/bokstaven som klienten skickat OM det finns några gissningar kvar.
            while((guess = dataIn.readUTF()) != null && guesses >= 1)  {
                //Om Guess bara är ett tecken så körs denna del
                if(guess.length() == 1){
                    //kallar på guessChar som kollar om tecknet finns i ordet.
                    playResult = guessChar(guess.charAt(0), playResult, splitGameWord);

                    //om playresult är detsamma som ordet vi ska gissa så sätts step till 1 för att användas i switchen nedan.
                    if(new String(playResult).equalsIgnoreCase(gameWord)){
                        step = 1;
                    }
                    //Switch som bestämmer om guesses skall minskas eller om scoreboard skall ökas.
                    switch(step) {
                        case 0:

                            if (hitOrMiss == false) {
                                guesses--;
                            }

                            dataOut.writeUTF(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                            dataOut.flush();
                            break;
                        //det rätta ordet är hittat inom rätt antal försök och scoreboard inkrementeras med ett plus att användaren kan välja att skapa ett nytt spel
                        case 1:
                            step = 0;
                            scoreBoard++;
                            dataOut.writeUTF("Congratulations you guessed the correct word. \nYour score is: " + scoreBoard +
                                    "\nDo you want to restart the game write yes or quit to exit");
                            if((guess = dataIn.readUTF()).equalsIgnoreCase("yes")){
                                startGame();
                            }
                            dataOut.flush();
                            break;
                    }
                }
                //om klienten chansar på ett ord som är längre än ett tecken
                if(guess.length() > 1){
                    if(guess.equalsIgnoreCase(gameWord)) {
                        scoreBoard++;
                        dataOut.writeUTF("Congratulations you guessed the correct word. " + guess + "\nYour score is: " + scoreBoard);
                        dataOut.writeUTF("Do you want to restart the game write yes or quit to exit");
                        if((guess = dataIn.readUTF()).equalsIgnoreCase("yes")){
                            startGame();
                        }else if((guess = dataIn.readUTF()).equalsIgnoreCase("yes")){

                        }
                        dataOut.flush();

                    }


                    else{

                        guesses--;
                        dataOut.writeUTF("Wrong word, try again");
                        dataOut.writeUTF(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                        dataOut.flush();
                    }

                }

            }
            scoreBoard--;
            dataOut.writeUTF("You have no guesses left \nYour score is: " + scoreBoard + "\nTo restart the game write yes or quit to exit");
            if(guess.equalsIgnoreCase("yes")){
                startGame();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void hangManWord(){ //Tar filen words.txt och kopierar alla ord till en ordlista
        words = new ArrayList<String>();

        try {
            File file = new File("/Users/Joaki/IdeaProjects/KTH-Skola/HomeWorkOne//src/Server/model/words.txt");
            BufferedReader read = new BufferedReader(new FileReader(file));         //BufferedReader.readLine() reads a line of text
            String line;
            try {
                while((line = read.readLine()) != null){
                    words.add(line);
                }
                read.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String randomWord(){     //Tar ut ett random ord från ordlistan (words)

        int index = (int) (random() * words.size());
        return words.get(index).toLowerCase();
    }


    public char[] guessChar(char guess, char[] progressionWord, char[] word) {
        hitOrMiss = false;
        for (int i = 0; i < word.length; i++) {
            if (word[i] == guess) {
                hitOrMiss = true;
                progressionWord[i] = guess;
            }

        }
        return progressionWord;
    }

}
