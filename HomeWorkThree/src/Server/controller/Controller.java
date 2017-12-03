package Server.controller;

import Server.model.FileTransferServer;
import Server.model.UserHandler;
import common.FileCredentials;
import common.FileServer;
import common.UserCredentials;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class Controller extends UnicastRemoteObject implements FileServer {
    private UserHandler userHandler = new UserHandler();
    private FileTransferServer fileTransferServer = new FileTransferServer();

    public Controller() throws IOException {

    }

    @Override
    public String registerNewUser(UserCredentials userCredentials) throws RemoteException, SQLException {

        return userHandler.registerUser(userCredentials);
    }

    @Override
    public String deleteUser(UserCredentials userCredentials) throws RemoteException, SQLException {
        return userHandler.deleteUser(userCredentials);
    }

    @Override
    public UserCredentials login(UserCredentials userCredentials) throws RemoteException, SQLException, ClassNotFoundException {

        return (userHandler.login(userCredentials));

    }
    @Override
    public void socketsClose() throws IOException {
        fileTransferServer.socketsClose();
    }

    @Override
    public String logOut(UserCredentials userCredentials) throws RemoteException {
        return null;
    }

    @Override
    public String listFiles(UserCredentials userCredentials) throws IOException, SQLException {
        return fileTransferServer.listFiles(userCredentials);
    }

    @Override
    public void startFileServerSocket() throws IOException {
        fileTransferServer.startFileServerSocket();
        //return false;
    }

    @Override
    public void serverSocketAccept() throws IOException {
        fileTransferServer.serverSocketAccept();
       // return true;
    }

    @Override
    public boolean uploadToDb(FileCredentials fileCredentials) throws IOException, SQLException {
        return fileTransferServer.uploadFile(fileCredentials);

    }

    @Override
    public void downloadFile(FileCredentials fileCredentials) throws IOException, SQLException {
        fileTransferServer.downloadFile(fileCredentials);
    }

    @Override
    public String deleteFile(FileCredentials fileCredentials) throws IOException, SQLException {
        return fileTransferServer.deleteFiles(fileCredentials);
    }

}
