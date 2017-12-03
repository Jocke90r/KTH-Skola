package Server.model;

import common.FileCredentials;
import common.UserCredentials;

import java.io.InputStream;
import java.sql.*;

public class DbHandler {
    Connection connection;
    Statement myStat;
    private PreparedStatement createUser;
    private PreparedStatement getAllUsers;
    private PreparedStatement checkIfUserLoggedIn;
    private PreparedStatement deleteUser;
    private PreparedStatement deleteFile;
    private PreparedStatement uploadFile;
    private PreparedStatement getFileName;
    private PreparedStatement downloadFile;
    private PreparedStatement listAllFiles;
    private PreparedStatement checkUserPermission;
    private boolean fileAlreadyInDb;
    private ResultSet resultSet;

    public void accessDb() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + "homeworkthree", "root", "");
            this.myStat = connection.createStatement();
            prepareStatements(this.connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerNewUser(String userName, String passWord) throws SQLException {

        boolean checkIfUserExists = checkUser(userName);
        if (checkIfUserExists == true) {
            System.out.println(userName);
            System.out.println(passWord);
            createUser.setString(1, userName);
            createUser.setString(2, passWord);
            createUser.executeUpdate();
            return true;
        } else return false;


    }

    private boolean checkUser(String userName) throws SQLException {
        ResultSet usersInDb = getAllUsers.executeQuery();
        while (usersInDb.next()) {
            if (usersInDb.getString(2).equalsIgnoreCase(userName)) {
                return false;
            }
        }
        return true;
    }

    private void prepareStatements(Connection connection) throws SQLException {
        createUser = connection.prepareStatement("INSERT INTO user (username,password) VALUES (?,?)");
        getAllUsers = connection.prepareStatement("SELECT * FROM user");
        checkIfUserLoggedIn = connection.prepareStatement("SELECT id FROM user WHERE username = ? AND password = ?");
        deleteUser = connection.prepareStatement("DELETE FROM user WHERE username =? AND password=?");
        deleteFile = connection.prepareStatement("DELETE FROM files WHERE name = ? AND (publik = 1 OR id =?)");
        uploadFile = connection.prepareStatement("INSERT INTO files (id,name,size,publik,file)VALUES (?,?,?,?,?)");
        getFileName = connection.prepareStatement("SELECT * FROM files");
        downloadFile = connection.prepareStatement("SELECT file FROM files WHERE name = ? AND (publik = 1 OR id = ?)");
        listAllFiles = connection.prepareStatement("SELECT * FROM files WHERE publik =1 OR id = ?");

    }

    public int login(UserCredentials userCredentials) throws SQLException {
        checkIfUserLoggedIn.setString(1, userCredentials.getUsername());
        checkIfUserLoggedIn.setString(2, userCredentials.getPassword());
        ResultSet userId = checkIfUserLoggedIn.executeQuery();
        while (userId.next()) {
            return userId.getInt("id");
        }
        return -1;
    }

    public boolean deleteUser(UserCredentials userCredentials) throws SQLException {
        deleteUser.setString(1, userCredentials.getUsername());
        deleteUser.setString(2, userCredentials.getPassword());
        deleteUser.executeUpdate();
        return true;
    }

    public boolean uploadFileToServer(InputStream data, FileCredentials fileCredentials) throws SQLException {
        accessDb();
        fileAlreadyInDb = checkFileName(fileCredentials);
        if (fileAlreadyInDb == false) {
            System.out.println("Vi är i kollen om filen redan finns i databasen.");
            return true;
        }
        uploadFile.setInt(1, fileCredentials.getOwnerId());
        uploadFile.setString(2, fileCredentials.getFilename());
        uploadFile.setInt(3, 313);
        uploadFile.setBoolean(4, fileCredentials.getPublik());
        uploadFile.setBlob(5, data);
        System.out.println("före execute");
        uploadFile.executeUpdate();
        return false;
    }

    public boolean checkFileName(FileCredentials fileCredentials) throws SQLException {
        resultSet = getFileName.executeQuery();
        while (resultSet.next()) {
            if (resultSet.getString(2).equalsIgnoreCase(fileCredentials.getFilename())) {
                return false;
            }

        }
        return true;
    }

    public boolean checkUserPermission(int userId, String fileName) throws SQLException {
        resultSet = checkUserPermission.executeQuery();
        while (resultSet.next()) {
            if (resultSet.getInt(1)==(userId) && resultSet.getString(2).equalsIgnoreCase(fileName)) {
                System.out.print("test");
                return true;
            }
        }
        return false;
    }

    public InputStream downloadFile(FileCredentials fileCredentials) throws SQLException {
        accessDb();
        downloadFile.setInt(2, fileCredentials.getOwnerId());
        downloadFile.setString(1, fileCredentials.getFilename());
        resultSet = downloadFile.executeQuery();
        InputStream input = null;
        if (resultSet.next()) {
            input = resultSet.getBinaryStream("file");
        }
        return input;
    }

    public String listAllFiles(UserCredentials userCredentials) throws SQLException {
        accessDb();
        StringBuilder fileNames = new StringBuilder();
        listAllFiles.setInt(1, userCredentials.getId());

        resultSet = listAllFiles.executeQuery();
        while (resultSet.next()) {
            fileNames.append(resultSet.getString(2) + "\n");
        }

        return fileNames.toString();
    }
    public String deleteFile(FileCredentials fileCredentials) throws SQLException {
        accessDb();

        deleteFile.setInt(2,fileCredentials.getOwnerId());
        deleteFile.setString(1,fileCredentials.getFilename());

        if(checkFileName(fileCredentials) == true){
            return "filename not found in database";
        }
        deleteFile.executeUpdate();
        if (checkFileName(fileCredentials)== false){

         return "file not deleted";
        }



        return "Success";
    }

}
