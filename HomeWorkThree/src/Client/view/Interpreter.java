package Client.view;

import common.FileCredentials;
import common.FileServer;
import common.UserCredentials;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Scanner;

public class Interpreter implements Runnable {
    private FileServer server;
    private boolean publik;
    private boolean read = true;
    private String filename;
    private Socket socket;
    private boolean fileAlreadyInDb;
    FileOutputStream fileOutputStream;
    InputStream inputStream;
    FileCredentials fileCredentials = new FileCredentials();
    FileCredentials downloadFileCredentials = new FileCredentials();
    private String downloadFileName;
    private String username;
    private String password;

    public void start() {
        new Thread(this).start();
    }

    UserCredentials userCredentials = new UserCredentials();
    Scanner console = new Scanner(System.in);

    @Override
    public void run() {
        try {
            lookupServerName();
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Specify a command " + "\nThe first command should be login to get the correct privileges");
        while (read == true) {
            System.out.println("give command");
            String input = console.nextLine();
            try {
                switch (input.toLowerCase()) {
                    case "login":
                        login();
                        break;
                    case "register":
                        register();
                        break;
                    case "logout":
                        logOut();
                        break;
                    case "deleteuser":
                        deleteUser();
                        break;
                    case "listfiles":
                        listFiles();
                        break;
                    case "uploadfile":
                        upload();
                        break;
                    case "downloadfile":
                        downloadFile();

                        break;
                    case "deletefile":
                        deleteFile();
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    private void logOut() {
        userCredentials.setLoggedIn(false);
        if (userCredentials.getId() < 0) {
            System.out.println("You werent logged in from the start");

        }
        if (userCredentials.getLoggedIn() == false) {
            System.out.println("You have successfully logged out user " + userCredentials.getUsername());

        }
        System.out.println("Failed to logout");

    }

    private void deleteUser() throws RemoteException, SQLException {
        if (userCredentials.getLoggedIn() == true) {
            System.out.println("Do you really want to delete THIS user from the database? " + "\n write yes for deletion");
            if (console.nextLine().equalsIgnoreCase("yes")) {
                System.out.println(server.deleteUser(userCredentials));
                userCredentials.setLoggedIn(false);
                userCredentials.setId(-1);
            }
        } else
            System.out.println("You cant enter deletemode without being logged in,");
    }

    private void deleteFile() throws IOException, SQLException {
        if (userCredentials.getLoggedIn() == true) {
            System.out.println("name the file you want to delete");
            fileCredentials.setFilename(console.nextLine());
            fileCredentials.setOwnerId(userCredentials.getId());
            System.out.println(server.deleteFile(fileCredentials));

        }
        System.out.println("You are not logged in, type login and enter your credentials");

    }

    private void listFiles() throws IOException, SQLException {
        if (userCredentials.getLoggedIn() == true) {
            server.startFileServerSocket();
            connectToFileServer();
            server.serverSocketAccept();
            System.out.println(server.listFiles(userCredentials));
        }
        System.out.println("You are not logged in, type login and enter your credentials");
    }

    private void upload() throws IOException, SQLException {
        if (userCredentials.getLoggedIn() == true) {
            boolean fileNameError = false;
            server.startFileServerSocket();
            connectToFileServer();
            server.serverSocketAccept();
            System.out.println("if this is a public file, type yes");
            if (console.nextLine().equalsIgnoreCase("yes")) {
                publik = true;
            } else {
                publik = false;
            }
            System.out.println("Write the name of the file");
            filename = console.nextLine();
            fileCredentials.setFilename(filename);
            fileCredentials.setPublik(publik);
            fileCredentials.setOwnerId(userCredentials.getId());
            System.out.println("Please provide the correct filepath");
            uploadFile(console.nextLine());
            fileAlreadyInDb = server.uploadToDb(fileCredentials);
            if (fileAlreadyInDb == true) {
                System.out.println("Filename " + fileCredentials.getFilename() + " try uploading the file again");
                fileNameError = true;
            }

            if (fileNameError == false) {
                System.out.println("File successfully uploaded");
            }
            server.socketsClose();
        }
        if (userCredentials.getLoggedIn() == false) {
            System.out.println("You are not logged in, type login and enter your credentials");
        }
    }

    //Letar efter en server med namnet FILE_SERVER som är definierat i variabeln "SERVER_NAME_IN_REGISTER"
    //Typecastar till FileServer då det är det som vi förväntar oss skall finnas där.
    private void lookupServerName() throws NotBoundException, MalformedURLException,
            RemoteException {
        server = (FileServer) Naming.lookup(
                "//localhost/" + FileServer.SERVER_NAME_IN_REGISTRY);
    }

    private void connectToFileServer() throws IOException {
        socket = new Socket("localhost", 3333);
    }

    private void login() throws ClassNotFoundException, RemoteException, SQLException {
        System.out.println("LOGIN " + " type username");
        username = console.nextLine();
        System.out.println("Type password ");
        password = console.nextLine();
        userCredentials = server.login(new UserCredentials(username, password));
        if (userCredentials.getLoggedIn() == true) {
            System.out.println("You are logged in");
        } else {
            System.out.println("Falied to login");
        }

    }

    private void register() throws RemoteException, SQLException {
        if (userCredentials.getLoggedIn() == false) {
            System.out.println("REGISTER " + " type username");
            username = console.nextLine();
            System.out.println("Type password ");
            password = console.nextLine();
            System.out.println(server.registerNewUser(userCredentials = new UserCredentials(username, password)));
        }
    }

    private void uploadFile(String path) throws IOException {
        File transferFile = new File(path);
        byte[] bytearray = new byte[(int) transferFile.length()];
        FileInputStream fin = new FileInputStream(transferFile);
        BufferedInputStream bin = new BufferedInputStream(fin);
        //läser filen från byte 0 till bytearrayens sista byte.
        bin.read(bytearray, 0, bytearray.length);
        OutputStream os = socket.getOutputStream();
        //Skickar filen
        os.write(bytearray, 0, bytearray.length);
        os.flush();
        socket.close();
    }

    private void downloadFile() throws IOException, SQLException {
        if (userCredentials.getLoggedIn() == false) {
            System.out.println("You need to be logged in to access this feature");
        }
        server.startFileServerSocket();
        connectToFileServer();
        server.serverSocketAccept();
        System.out.println("name the files name");
        downloadFileName = console.nextLine();
        downloadFileCredentials.setFilename(downloadFileName);
        downloadFileCredentials.setOwnerId(userCredentials.getId());
        server.downloadFile(downloadFileCredentials);
        File theFile = new File(downloadFileName + ".pdf");
        fileOutputStream = new FileOutputStream(theFile);
        inputStream = socket.getInputStream();
        byte[] buffer = new byte[4096];
        while (inputStream.read(buffer) > 0) {
            fileOutputStream.write(buffer);
        }
        System.out.println("File download completed");
        socket.close();


    }

}
