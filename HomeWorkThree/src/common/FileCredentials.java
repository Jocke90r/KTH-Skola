package common;

import java.io.Serializable;

/**
 *
 */
public class FileCredentials implements Serializable {

    private  String filename;
    private boolean publik;
    private int ownerId;


    public void setFilename(String filename){
        this.filename = filename;
    }
    public String getFilename(){
        return this.filename;
    }
    public int getOwnerId(){return this.ownerId;}
    public void setPublik(Boolean publik){
        this.publik = publik;
    }
    public boolean getPublik(){
        return this.publik;
    }
    public void setOwnerId(int ownerId){
        this.ownerId = ownerId;
    }












}




