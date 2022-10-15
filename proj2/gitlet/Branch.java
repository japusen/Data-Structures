package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

public class Branch implements Serializable {

    static final File BRANCH_FILE = Utils.join(Repository.GITLET_DIR, "branches");
    private static TreeMap<String, String> branches;

    Branch() {
        branches = new TreeMap<>();
    }

    public void updateMaster(String commit) {
        branches.put("master", commit);
    }

    public void updateHEAD(String commit) {
        branches.put("head", commit);
    }

    public void newBranch(String branch, String commit) {
        branches.put(branch, commit);
    }

    /** Returns the branch object from the branch file */
    public static Branch fromFile() {
        Branch branch = Utils.readObject(BRANCH_FILE, Branch.class);
        return branch;
    }

    /** Saves the state of the branch to the file */
    public void saveBranch() {
        Utils.writeObject(BRANCH_FILE, this);
    }

}
