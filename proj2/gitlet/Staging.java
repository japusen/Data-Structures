package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;



public class Staging implements Serializable {

    static final File STAGING_FILE = Utils.join(Repository.GITLET_DIR, "staging");

    /** Files in staging area to add to commit : File name -> blobID */
    private static TreeMap<String, String> add;

    /** Files in staging area to remove from commit : File name -> blobID */
    private static TreeMap<String, String> remove;

    Staging() {
        add = new TreeMap<>();
        remove = new TreeMap<>();
    }

    /** Returns the staging area from the staging file */
    public static Staging fromFile() {
        Staging stagingArea = Utils.readObject(STAGING_FILE, Staging.class);
        return stagingArea;
    }

    /** Saves the state of the staging area to the file */
    public void saveStagingArea() {
        Utils.writeObject(STAGING_FILE, this);
    }


    /** Stages the file for adding */
    public void stageAdd(String fileName, String blobID) {
        add.put(fileName, blobID);
    }

    /** Cancels the add for the file */
    public void cancelAdd(String fileName) {
        add.remove(fileName);
    }

    /** Stages the file for removal */
    public void stageRemove(String fileName, String blobID) {
        remove.put(fileName, blobID);
    }

    /** Cancels the remove for the file */
    public void cancelRemove(String fileName) {
        remove.remove(fileName);
    }

}
