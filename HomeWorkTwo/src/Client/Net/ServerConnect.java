package Client.Net;

import Client.controller.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by Chosrat on 2017-11-21.
 */
public class ServerConnect implements Runnable{

    private SocketChannel socketChannel;
    private Selector selector;
    private InetSocketAddress serverAddress = new InetSocketAddress("localhost", 3333);
    public ByteBuffer messageToServer;
    private boolean write;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

    public void connect(){
        new Thread(this).start();
    }

    @Override
    public void run(){
        try {
            //configureConnection();
            initializeSelector();

            while (true) {

                if(write){
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    write = false;
                }
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                  //  System.out.println("while keys");

                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
              //          System.out.println("Inne i Client isValid");
                        continue;
                    } else if(key.isConnectable()){

                //        System.out.println("Inne i Client isConnectable");
                        configureConnection(key);
                    }
                    else if (key.isReadable()) {

                  //      System.out.println("Inne i Client isReadable");
                        readFromServer(key);
                    } else if (key.isWritable()) {

                    //    System.out.println("Inne i Client isWritable");
                        writeToServer(key);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromServer(SelectionKey key) throws IOException{
        ByteBuffer bufferFromServer = ByteBuffer.allocate(1000);
        SocketChannel channel = (SocketChannel) key.channel();
        //bufferFromServer = (ByteBuffer) key.attachment();
        bufferFromServer.clear();
        channel.read(bufferFromServer);
        bufferFromServer.flip();
        byte[] bytes = new byte[bufferFromServer.remaining()];
        bufferFromServer.get(bytes); //skriver till bytes
    //    System.out.println("Clienten readFromServer");
        String fromServer = new String(bytes, "UTF-8");
        System.out.println(fromServer);
        key.interestOps(SelectionKey.OP_WRITE);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeToServer(SelectionKey key) throws IOException{

        while(!messagesToSend.isEmpty()){
            socketChannel.write(messagesToSend.poll());

        }
        key.interestOps(SelectionKey.OP_READ);
    //    System.out.println("Inne i writetoserver");

    }

    private void configureConnection(SelectionKey key) throws IOException {


        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
    }

    private void initializeSelector() throws IOException{
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    public  void messageHandler(String msg) {
        messageToServer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
        messagesToSend.add(messageToServer);
        //System.out.println(msg);
        write = true;
        selector.wakeup();
      //  System.out.println("clienten messageHandler");
    }

    public void disConnect() throws IOException{
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();

    }
}
