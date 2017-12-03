package Server.model;

import common.UserCredentials;

import java.sql.SQLException;

public class UserHandler {
    DbHandler dbHandler;

    public UserHandler() {
        dbHandler = new DbHandler();
        dbHandler.accessDb();
    }

    public String registerUser(UserCredentials userCredentials) throws SQLException {
        userCredentials.setLoggedIn(dbHandler.registerNewUser(userCredentials.getUsername(), userCredentials.getPassword()));
        if (userCredentials.getLoggedIn() == true) {
            return "You are registered and logged in";/*Kolla om användaren finns, skapa om den inte fanns. returnera false ifall användaren redan fanns. */
        } else
            return "unable to register, user already exists ";
    }

    public String deleteUser(UserCredentials userCredentials) throws SQLException {
        dbHandler.deleteUser(userCredentials);
        return "User Deleted";
    }

    public UserCredentials login(UserCredentials userCredentials) throws SQLException, ClassNotFoundException {
        userCredentials.setId(dbHandler.login(userCredentials));

        if (userCredentials.getId() >= 0) {
            userCredentials.setLoggedIn(true);
        }
        return userCredentials;
    }

    public String logOut(UserCredentials userCredentials) {

        return null;
    }

    public Boolean deleteFile(UserCredentials userCredentials, String fileName) {
        return null;
    }
}
