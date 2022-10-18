package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  Create commits in the commit directory and relays information about individual commits
 *
 *  @author Julius Apusen
 */
public class Commit implements Serializable, Dumpable {
    /**
     * message -- The message of this Commit
     * parent -- The previous commit
     * time -- The date and time of the commit
     * commitFiles -- The names of the files being tracked : File name -> blobID
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String message;
    private String parentID;
    private String time;
    private TreeMap<String, String> commitFiles;


    public Commit(String message, String parent, String time, TreeMap<String, String> commitFiles) {
        this.message = message;
        this.parentID = parent;
        this.time = time;
        this.commitFiles = commitFiles;
    }

    /** Saves a Commit to a file for future use. */
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

    /** Returns the message of the commit */
    public String getMessage() {
        return message;
    }

    /** Returns the map of commit files */
    public TreeMap<String, String> getCommitFiles() {
        return commitFiles;
    }

    /** Returns the blobID for the file */
    public String getFileBlobID(String fileName) {
        return commitFiles.get(fileName);
    }

    /** Returns true if the Commit is tracking the file fileName */
    public boolean isTracking(String fileName) {
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
