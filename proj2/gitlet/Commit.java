package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Julius Apusen
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */



    /** The message of this Commit. */
    private String message;

    /** The previous commit */
    private String parent;

    /** The date and time of the commit */
    private Date time;

    /** The names of the files and the blobIDs : File name -> blobID */
    private TreeMap<String, String> nameBlopsMap;

    public Commit(String message, String parent, Date time, TreeMap<String, String> nameBlopsMap) {
        this.message = message;
        this.parent = parent;
        this.time = time;
        this.nameBlopsMap = nameBlopsMap;
    }

    /**
     * Reads in and deserializes a Commit from a file with given commitID in COMMIT_DIR.
     *
     * @param commitID Name of commit to load
     * @return Commit read from file
     */
    public static Commit fromFile(String commitID) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }

    /**
     * Saves a Commit to a file for future use.
     */
    public void saveCommitToDir() {
        String commitID = getCommitID();
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        if (!commitFile.exists()) {
            try {
                commitFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(commitFile, this);
    }

    /** Returns the ID of the commit */
    public String getCommitID() {
        return Utils.sha1(Utils.serialize(this));
    }

    /** Returns true if the Commit has the file fileName */
    public boolean containsFile(String fileName) {
        return nameBlopsMap.containsKey(fileName);
    }

    /** Returns the blobID for the associated fileName */
    public String getBlobID(String fileName) {
        return nameBlopsMap.get(fileName);
    }

}
