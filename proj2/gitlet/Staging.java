package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;



public class Staging implements Serializable, Dumpable {

    static final File STAGING_FILE = Utils.join(Repository.GITLET_DIR, "staging");

    /** Files in staging area to add to commit : File name -> blobID */
    private TreeMap<String, String> add;

    /** Files in staging area to remove from commit : File name -> blobID */
    private TreeMap<String, String> remove;

    Staging() {
        add = new TreeMap<>();
        remove = new TreeMap<>();
    }

    /** Saves the state of the staging area to the file */
    public void saveToFile() {
        Utils.writeObject(STAGING_FILE, this);
    }

    /** Returns whether the file fileName is in the staging area */
    public boolean containsFile(String fileName) {
        return add.containsKey(fileName);
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

    /** Cancels the removal of a file */
    public void cancelRemove(String fileName) {
        remove.remove(fileName);
    }

    /** Empties the staging area */
    public void clear() {
        add = new TreeMap<>();
        remove = new TreeMap<>();
    }

    /** Returns the map with the files to be added to a commit */
    public TreeMap<String, String> getAdd() {
        return add;
    }

    /** Returns the map with the files to be removed from a commit */
    public TreeMap<String, String> getRemove() {
        return remove;
    }

    @Override
    public void dump() {
        System.out.printf("Staging Area%nAdd: %s%nRemove: %s%n", add, remove);
    }
}
