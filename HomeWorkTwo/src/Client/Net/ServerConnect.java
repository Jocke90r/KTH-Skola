package Client.Net;


import Client.View.Interpreter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Joakim on 2017-11-21.
 */
public class ServerConnect implements Runnable{

    private SocketChannel socketChannel;
    private Selector selector;
    private InetSocketAddress serverAddress = new InetSocketAddress("localhost", 3333);
    public ByteBuffer messageToServer;
    private boolean write;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

    //Starta ny tråd för klienten som i sin tur startar run
    public void connect(){
        new Thread(this).start();
    }

    @Override
    public void run(){
        try {
           // Initialisera selectorn
            initializeSelector();

            while (true) {

                if(write){ //Om det finns meddelanden i kön så ändrar vi operation till "WRITE" för att kunna skriva.
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    write = false;
                }
                //Kollar vilken typ av key som finns och kör därefter den metoden som tillhör det.
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    } else if(key.isConnectable()){
                        configureConnection(key);
                    }
                    else if (key.isReadable()) {
                         readFromServer(key);
                    } else if (key.isWritable()) {
                         writeToServer(key);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * Läser från servern och lämnar tillbaka en tråd och hämtar en ny via forkjoinpool för att Servern skall kunna fortsätta köra
    * */
    private void readFromServer(SelectionKey key) throws IOException{
        ByteBuffer bufferFromServer = ByteBuffer.allocate(256);
        SocketChannel channel = (SocketChannel) key.channel();
        bufferFromServer.clear();
        channel.read(bufferFromServer);
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(()->{

            try {
                bufferFromServer.flip();
                byte[] bytes = new byte[bufferFromServer.remaining()];
                bufferFromServer.get(bytes); //skriver till bytes
                String fromServer = null;
                fromServer = new String(bytes, "UTF-8");
                Interpreter interpreter = new Interpreter();
                interpreter.writeMessage(fromServer);
                key.interestOps(SelectionKey.OP_WRITE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            });



    }
    //Skriver till servern om det finns ett meddelande i kön.
    private void writeToServer(SelectionKey key) throws IOException{

        while(!messagesToSend.isEmpty()){
            socketChannel.write(messagesToSend.poll());

        }
        key.interestOps(SelectionKey.OP_READ);
    }
    //skapar uppkoppling
    private void configureConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
    }

    //inställningarna för selector och socketchannel för att kunna koppla upp mot servern.
    private void initializeSelector() throws IOException{
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
    //Tar emot meddelande från klienten och skickar till servern.
    public  void messageHandler(String msg) {
        messageToServer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
        messagesToSend.add(messageToServer);
        //System.out.println(msg);
        write = true;
        selector.wakeup();
      }
    //avslutar uppkopplingen
    public void disConnect() throws IOException{
        socketChannel.keyFor(selector).cancel();
        socketChannel.close();
        System.out.println("Connection Closed");

    }
}
