package Server.Model;

/**
 * Created by Joakim on 2017-11-09.
 */

import Server.Net.HangmanServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.random;


public class Hangman {

    public ArrayList<String> words;
    private int guesses;
    private int scoreBoard;
    public String gameWord;
    public char[] splitGameWord;
    public char[] playResult;
    private int step = 0;
    boolean hitOrMiss;
    private String guess = null;

  /*  public void setGuess(String msg) {
        this.guess = msg;
    }




    public void startGame() {
        HangmanServer hangmanServer = new HangmanServer();
        hangManWord();                              //skapar en lista med ord
        gameWord = randomWord();                    //hämtar ett ord från listan som innehåller alla ord
        splitGameWord = gameWord.toCharArray();     //delar upp ordet till en array av chars för att kunna jämföra.
        playResult = new char[splitGameWord.length];//skapar en chararray med så många tecken som spelordet är på.
        guesses = splitGameWord.length;             //sätter antalet gissningar till längden på ordet
        System.out.println(gameWord);               //skapar CHEEEATS om man har tillgång till servern

       if(true) {
           System.out.println("före hangmanserver");
           hangmanServer.messageHandler("Start game: You have " );
           System.out.println("efter hangmanserver");
       }
        //om det finns något i dataIn så sätts guess till det ordet/bokstaven som klienten skickat OM det finns några gissningar kvar.
        while (guess != null && guesses >= 1) {
            //Om Guess bara är ett tecken så körs denna del
            if (guess.length() == 1) {
                //kallar på guessChar som kollar om tecknet finns i ordet.
                playResult = guessChar(guess.charAt(0), playResult, splitGameWord);

                //om playresult är detsamma som ordet vi ska gissa så sätts step till 1 för att användas i switchen nedan.
                if (new String(playResult).equalsIgnoreCase(gameWord)) {
                    step = 1;
                }
                //Switch som bestämmer om guesses skall minskas eller om scoreboard skall ökas.
                switch (step) {
                    case 0:

                        if (hitOrMiss == false) {
                            guesses--;
                        }

                        hangmanServer.messageHandler(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                        break;
                    //det rätta ordet är hittat inom rätt antal försök och scoreboard inkrementeras med ett plus att användaren kan välja att skapa ett nytt spel
                    case 1:
                        step = 0;
                        scoreBoard++;
                        hangmanServer.messageHandler("Congratulations you guessed the correct word. \nYour score is: " + scoreBoard +
                                "\nDo you want to restart the game write yes or quit to exit");
                        if ((guess.equalsIgnoreCase("yes"))) {
                            startGame();
                        }

                        break;
                }
            }
            //om klienten chansar på ett ord som är längre än ett tecken
            if (guess.length() > 1) {
                if (guess.equalsIgnoreCase(gameWord)) {
                    scoreBoard++;
                    hangmanServer.messageHandler("Congratulations you guessed the correct word. " + guess + "\nYour score is: " + scoreBoard);
                    hangmanServer.messageHandler("Do you want to restart the game write yes or quit to exit");
                    if ((guess.equalsIgnoreCase("yes"))) {
                        startGame();
                    }
                } else if(guess.equalsIgnoreCase("yes")){
                    guesses--;
                    hangmanServer.messageHandler("Wrong word, try again");
                    hangmanServer.messageHandler(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                }


            }
        }

        if (guess != null) {
            scoreBoard--;
            hangmanServer.messageHandler("You have no guesses left \nYour score is: " + scoreBoard + "\nTo restart the game write yes or quit to exit");
            if (guess.equalsIgnoreCase("yes")) {
                startGame();
            }else if(guess.equalsIgnoreCase("quit")){

            }
        }

    }


    public void hangManWord() { //Tar filen words.txt och kopierar alla ord till en ordlista
        words = new ArrayList<String>();

        try {
            File file = new File("/Users/Joaki/IdeaProjects/KTH-Skola/HomeWorkOne//src/Server/model/words.txt");
            BufferedReader read = new BufferedReader(new FileReader(file));         //BufferedReader.readLine() reads a line of text
            String line;
            try {
                while ((line = read.readLine()) != null) {
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

    public String randomWord() {     //Tar ut ett random ord från ordlistan (words)

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
*/
}
