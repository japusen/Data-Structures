package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

public class Staging implements Serializable {

    static final File STAGING_FILE = Utils.join(Repository.GITLET_DIR, "staging");

    /** Files in staging area to add to commit */
    private static TreeMap<String, String> add;

    /** Files in staging area to remove from commit */
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
    public void saveStaging() {
        Utils.writeObject(STAGING_FILE, this);
    }

}
