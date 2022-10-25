package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;


/** Represents a gitlet commit object.
 *  Create commits in the commit directory and relays information about individual commits
 *
 *  @author Julius Apusen
 */
public class Commit implements Serializable, Dumpable {
    /**
     * message -- The message of this Commit
     * parentID -- The previous commit ID
     * secondParentID -- in the case of a merge commit, the commit ID of the merged branch
     * time -- The date and time of the commit
     * commitFiles -- The names of the files being tracked : File name -> blobID
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private final String message;
    private final String parentID;
    private final String secondParentID;
    private final String time;
    private final TreeMap<String, String> commitFiles;


    public Commit(String message, String parent, String mergeParent, String time, TreeMap<String, String> commitFiles) {
        this.message = message;
        this.parentID = parent;
        this.secondParentID = mergeParent;
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

    /** Returns true if the commit has a second parent */
    public boolean hasSecondParent() {
        return secondParentID != null;
    }

    /** Returns the ID of the second parent commit */
    public String getSecondParentID() {
        return secondParentID;
    }

    /** Returns the message of the commit */
    public String getMessage() {
        return message;
    }

    /** Returns the map of commit files */
    public TreeMap<String, String> getCommitFiles() {
        return commitFiles;
    }

    /** Returns the blobID for the file or the empty string if it does not exist*/
    public String getFileBlobID(String fileName) {
        return commitFiles.getOrDefault(fileName, "");
    }

    /** Returns true if the Commit is tracking the file fileName */
    public boolean isTracking(String fileName) {
        return commitFiles.containsKey(fileName);
    }

    /** Returns the commit IDs of all commits along the path to the origin commit
     * starting from this commit and accounting for merge commits */
    public HashSet<String> getAllParents() {
        // Base case: Origin commit returns an empty set
        if (parentID == null) {
            return new HashSet<>();
        }

        // Add the current commit and the first parent
        HashSet<String> allParents = new HashSet<>();
        allParents.add(getCommitID());
        allParents.add(parentID);

        // Add the parents of the first parent
        Commit firstParent = Repository.loadCommitFromFile(parentID);
        HashSet<String> allFirstsParents = firstParent.getAllParents();
        allParents.addAll(allFirstsParents);

        // Add the parents of the second parent if there is one
        if (secondParentID != null) {
            Commit secondParent = Repository.loadCommitFromFile(secondParentID);
            HashSet<String> allSecondsParents = secondParent.getAllParents();
            allParents.addAll(allSecondsParents);
        }

        return allParents;
    }

    /** Returns a set of all the file names in the commit */
    public Set<String> getFileNames() {
        return commitFiles.keySet();
    }

    @Override
    public String toString() {
        if (secondParentID == null) {
            return "===\ncommit " + getCommitID() + "\nDate: "
                    + time.toString() + "\n" + message + "\n";
        } else {
            return "===\ncommit " + getCommitID() + "\nMerge: " + parentID.substring(0, 7)
                    + " " + secondParentID.substring(0, 7) + "\nDate: "
                    + time.toString() + "\n" + message + "\n";
        }
    }

    @Override
    public void dump() {
        System.out.printf("Commit%nID: %s%nParent: %s%nDate: %s%nMessage: %s%nFiles: %s%n",
                getCommitID(), parentID, time.toString(), message, commitFiles);
    }

}
