package Server.Net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
 * Created by Joakim on 2017-11-21.
 */
public class HangmanServer {

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private ServerSocket serverSocket;
    public ByteBuffer messageToClient;
    private boolean write;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

    private void run() {
        try {
            initializeSelector();
            initializeSocketChannel();

            while (true) {


                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator(); //Itererar igenom tillgängliga nycklar
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isAcceptable()) {
                        System.out.println("skapar uppkopplung");
                        clientConnect(key);
                    } else if(key.isReadable()){

                        //System.out.println("skapar uppkopung");

                        listenFromClient(key);
                    } else if(key.isWritable()){

                        messageHandler("tet");
                        writeToClient(key);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Skriva data till klienten från servern.
    private void writeToClient(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        while(!messagesToSend.isEmpty()){
            channel.write(messagesToSend.poll());
            System.out.println("Nu är vi i write server ");
        }
        key.interestOps(SelectionKey.OP_READ);

    }
    /*private void writeToClient(SelectionKey key) throws IOException{
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
    }*/

    //Tar emot data från klienten via channel
    private void listenFromClient(SelectionKey key) throws IOException{
        ByteBuffer Buffer = ByteBuffer.allocate(1000);
        SocketChannel channel = (SocketChannel) key.channel();
        Buffer = (ByteBuffer) key.attachment();
        Buffer.clear();

        channel.read(Buffer);
        Buffer.flip();
        byte[] bytes = new byte[Buffer.remaining()];
        Buffer.get(bytes); //skriver till bytes
        System.out.println("Läser " + new String(bytes, "UTF-8"));
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
        clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }
    public  void messageHandler(String msg) {
        String test = "hej nu har vi här";
        messageToClient = ByteBuffer.wrap(test.getBytes(StandardCharsets.UTF_8));
        messagesToSend.add(messageToClient);
        write=true;
        selector.wakeup();
    }

    public  static void main(String[] args){
        HangmanServer server = new HangmanServer();
        server.run();
    }
}

