package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Julius Apusen
 */
public class Commit implements Serializable, Dumpable {
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
    private String parentID;

    /** The date and time of the commit */
    private Date time;

    /** The names of the files and the blobIDs : File name -> blobID */
    private TreeMap<String, String> commitFiles;

    public Commit(String message, String parent, Date time, TreeMap<String, String> commitFiles) {
        this.message = message;
        this.parentID = parent;
        this.time = time;
        this.commitFiles = commitFiles;
    }

    /**
     * Saves a Commit to a file for future use.
     */
    public void saveToDir() {
        String commitID = getCommitID();
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        Utils.writeObject(commitFile, this);
    }

    /** Returns the ID of the commit */
    public String getCommitID() {
        return Utils.sha1(Utils.serialize(this));
    }

    /** Returns the ID of the parent commit */
    public String getParentID() {
        return parentID;
    }

    /** Returns the map of commit files */
    public TreeMap<String, String> getCommitFiles() {
        return commitFiles;
    }

    /** Returns true if the Commit is tracking the file fileName */
    public boolean containsFile(String fileName) {
        return commitFiles.containsKey(fileName);
    }

    /** Returns the blobID for the associated fileName or Null if it does not exist */
    public String getBlobID(String fileName) {
        return commitFiles.get(fileName);
    }

    /** Returns true if the commit is the origin commit */
    public boolean isOrigin() {
        return getCommitID() == Repository.originCommitID;
    }

    @Override
    public String toString() {
        return "===\ncommit " + getCommitID() + "\nDate: " + time.toString() + "\n" + message + "\n";
    }

    @Override
    public void dump() {
        System.out.printf("Commit%nID: %s%nParent: %s%nDate: %s%nMessage: %s%nFiles: %s%n",
                getCommitID(), parentID, time.toString(), message, commitFiles);
    }


}
