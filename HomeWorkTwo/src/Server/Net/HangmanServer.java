package Server.Net;

import Server.Model.Hangman;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by Chosrat on 2017-11-21.
 */
public class HangmanServer {

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private ServerSocket serverSocket;
    //public ByteBuffer messageToClient;
    private boolean write;

    //public Controller controller = new Controller();
    //Hangman hangman;
   // private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();


    private void run() {
        try {
            initializeSelector();
            initializeSocketChannel();
            while (true) {

          /*      if(write){
                    serverChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    write = false;
                }
*/
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator(); //Itererar igenom tillgängliga nycklar
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isAcceptable()) {
                 //       System.out.println("Inne i Server Acceptable");
                        clientConnect(key);
                       // hangman = new Hangman(this);
                    } if(key.isReadable()){

                  //      System.out.println("Inne i Server isReadable");

                        listenFromClient(key);
                    } if(key.isWritable()){
                       // messageHandler("Skriver från server writable till clienten");


                     //   System.out.println("Inne i Server isWritable");
                        writeToClient(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   /* //Skriva data till klienten från servern.
    private void writeToClient(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.flip();
        channel.write(buffer);
        if(buffer.hasRemaining()){
            buffer.compact();
        } else {
            buffer.clear();
        }
        key.interestOps(SelectionKey.OP_READ);
    } */

   private void writeToClient(SelectionKey key) throws IOException{
       SocketChannel channel = (SocketChannel) key.channel();
       Client client = (Client) key.attachment();
       while(!client.messagesToSend.isEmpty()){
           channel.write(client.messagesToSend.poll());

       }
       key.interestOps(SelectionKey.OP_READ);
     //  System.out.println("Inne i Write to client");

   }

    //Tar emot data från klienten via channel
    private void listenFromClient(SelectionKey key) throws IOException{
        ByteBuffer Buffer = ByteBuffer.allocate(1000);
        SocketChannel channel = (SocketChannel) key.channel();
        Buffer.clear();
        channel.read(Buffer);
        Buffer.flip();
        byte[] bytes = new byte[Buffer.remaining()];
        Buffer.get(bytes); //skriver till bytes
       // System.out.println("Inne i listenFromClient");
        String input = new String(bytes, "UTF-8");
        Client client = (Client) key.attachment();
        client.hangman.setGuess(input);
        client.hangman.gameLoop();
        //if(input.equalsIgnoreCase("yes"))
      //  {Hangman hangman = new Hangman(this);}
       // System.out.println(input);
        key.interestOps(SelectionKey.OP_WRITE);
    }


    //Skapar server och ställer in den till non-blocking och selector key till accept connection.
    private void initializeSocketChannel() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverSocket = serverChannel.socket();
        serverChannel.configureBlocking(false);
        serverSocket.bind(new InetSocketAddress(3333));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    }


    private void initializeSelector() throws IOException {
        selector = Selector.open();
    }

    //Accepterar klienten och skapar en uppkoppling
    private void clientConnect(SelectionKey key) throws IOException{

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        Client client = new Client();
        clientChannel.register(selector, SelectionKey.OP_READ, client);
    }





    public  static void main(String[] args){
        HangmanServer server = new HangmanServer();
        server.run();
    }
    public class Client{

        public Hangman hangman;
        public final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

        public Client(){
            hangman = new Hangman(this);
        }
        public void messageHandler(String msg) {
            ByteBuffer messageToClient = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
            messagesToSend.add(messageToClient);
            messageToClient.clear();
            write = true;
           // System.out.println("Inne i Server messageHandler");
            selector.wakeup();
        }
    }
}

