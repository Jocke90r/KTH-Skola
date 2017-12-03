package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {

    void ping() throws RemoteException;

    void notifyChanges() throws RemoteException;

}