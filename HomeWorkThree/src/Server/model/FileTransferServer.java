package Server.model;

import common.FileCredentials;
import common.UserCredentials;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class FileTransferServer {
    DbHandler dbHandler = new DbHandler();
    ServerSocket serverSocket = null;
    Socket socket = null;
    InputStream dataIn;
    OutputStream outputStream;


    public boolean uploadFile(FileCredentials fileCredentials) throws IOException, SQLException {

            dataIn = socket.getInputStream(); //input från klienten
        long test = 0;

            if (dataIn.available() > -1) {
                test = test =+ dataIn.available();
                System.out.println("testetsatsatastasta  " + test);
                return uploadToDatabase(dataIn, fileCredentials);
            }

            socketsClose();

        return true;
    }


    private boolean uploadToDatabase(InputStream data, FileCredentials fileCredentials) throws SQLException, IOException {
        return dbHandler.uploadFileToServer(data, fileCredentials);
    }

    public void downloadFile(FileCredentials fileCredentials) throws IOException, SQLException {
        InputStream theFile = dbHandler.downloadFile(fileCredentials);
        outputStream = socket.getOutputStream();
        byte[] buffer = new byte[4096];
        while (theFile.read(buffer) > 0) {
            outputStream.write(buffer);
        }
        outputStream.flush();
        socketsClose();
    }

    public String listFiles(UserCredentials userCredentials) throws SQLException, IOException {
        socketsClose();
        return dbHandler.listAllFiles(userCredentials);
    }

    public String deleteFiles(FileCredentials fileCredentials) throws SQLException, IOException {
        socketsClose();
        return dbHandler.deleteFile(fileCredentials);
    }
    public void startFileServerSocket() throws IOException {
        serverSocket = new ServerSocket(3333);
    }
    public void serverSocketAccept() throws IOException {
        socket = serverSocket.accept();
        System.out.println("Server accepted socket");
    }
    public void socketsClose() throws IOException {
        socket.close();
        serverSocket.close();
        System.out.println("Server closed all sockets");
    }
}
