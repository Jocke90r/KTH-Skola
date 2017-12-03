package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FileServer extends Remote {

    public static final String SERVER_NAME_IN_REGISTRY = "FILE_SERVER";

    String registerNewUser(UserCredentials userCredentials) throws RemoteException, SQLException;

    String deleteUser(UserCredentials userCredentials) throws RemoteException, SQLException;

    UserCredentials login (UserCredentials userCredentials) throws RemoteException, SQLException, ClassNotFoundException;

    String logOut(UserCredentials userCredentials) throws RemoteException;

    String listFiles(UserCredentials userCredentials) throws IOException, SQLException;

    void startFileServerSocket() throws IOException;

    void serverSocketAccept() throws IOException;

    boolean uploadToDb(FileCredentials fileCredentials) throws IOException, SQLException;

    void downloadFile(FileCredentials fileCredentials) throws IOException, SQLException;

    String deleteFile(FileCredentials fileCredentials) throws IOException, SQLException;

    //void register(Client client) throws RemoteException;
    void socketsClose() throws IOException,SQLException;

}
