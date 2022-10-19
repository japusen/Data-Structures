package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;


/** Represents a gitlet repository staging area
 *  Mantains the staging area of the repo
 *
 *  @author Julius Apusen
 */
public class Staging implements Serializable, Dumpable {
    /**
     * STAGING_FILE -- File that contains the serialized objects
     * add -- Files in staging area to add to commit : File name -> blobID
     * remove -- Files in staging area to remove from commit : File name -> blobID
     *
     */

    static final File STAGING_FILE = Utils.join(Repository.GITLET_DIR, "staging");
    private TreeMap<String, String> addedFiles;
    private TreeMap<String, String> removedFiles;

    Staging() {
        addedFiles = new TreeMap<>();
        removedFiles = new TreeMap<>();
    }

    /** Saves the state of the staging area to the file */
    public void saveToFile() {
        Utils.writeObject(STAGING_FILE, this);
    }

    /** Returns whether the file fileName is already staged for addition */
    public boolean hasAdded(String fileName) {
        return addedFiles.containsKey(fileName);
    }

    /** Returns whether the file fileName is already staged for removal */
    public boolean hasRemoved(String fileName) {
        return removedFiles.containsKey(fileName);
    }

    /** Stages the file for adding */
    public void addFile(String fileName, String blobID) {
        addedFiles.put(fileName, blobID);
    }

    /** Cancels the add for the file */
    public void cancelAdd(String fileName) {
        addedFiles.remove(fileName);
    }

    /** Stages the file for removal */
    public void removeFile(String fileName, String blobID) {
        removedFiles.put(fileName, blobID);
    }

    /** Cancels the removal of a file */
    public void cancelRemove(String fileName) {
        removedFiles.remove(fileName);
    }

    /** Empties the staging area */
    public void clear() {
        addedFiles = new TreeMap<>();
        removedFiles = new TreeMap<>();
    }

    /** Returns true if the staging area (addition and removal) is empty */
    public boolean isEmpty() {
        return addedFiles.isEmpty() && removedFiles.isEmpty();
    }

    /** Returns the map with the files to be added to a commit */
    public TreeMap<String, String> getAddedFiles() {
        return addedFiles;
    }

    /** Returns the map with the files to be removed from a commit */
    public TreeMap<String, String> getRemove() {
        return removedFiles;
    }

    /** Print out the files staged for addition and removal for the status command */
    public void printFiles() {
        System.out.println("=== Staged Files ===");
        for (String fileName : addedFiles.keySet()) {
            System.out.println(fileName);
        }
        System.out.println("\n=== Removed Files ===");
        for (String fileName : removedFiles.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    @Override
    public void dump() {
        System.out.printf("Staging Area%nAdd: %s%nRemove: %s%n", addedFiles, removedFiles);
    }
}
