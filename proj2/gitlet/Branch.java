package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

public class Branch implements Serializable {

    static final File BRANCH_FILE = Utils.join(Repository.GITLET_DIR, "branches");
    static final String HEAD = "HEAD";
    static final String MASTER = "Master";

    /** Mapping for branches : Branch Name -> hash of commit being pointed to */
    private static TreeMap<String, String> branches;

    Branch() {
        branches = new TreeMap<>();
    }

    /** Points the MASTER branch to the provided commit */
    public void updateMaster(String commit) {
        branches.put(MASTER, commit);
    }

    /** Points the HEAD branch to the provided commit */
    public void updateHEAD(String commit) {
        branches.put(HEAD, commit);
    }

    /** Adds a new branch with the corresponding commit */
    public void newBranch(String branch, String commit) {
        branches.put(branch, commit);
    }

    /** Returns the commit that HEAD points to */
    public static Commit getHEADCommit() {
        String headHash = branches.get(HEAD);
        return Commit.fromFile(headHash);
    }

    /** Returns the commit that Master points to */
    public static Commit getMasterCommit() {
        String masterHash = branches.get(MASTER);
        return Commit.fromFile(masterHash);
    }

    /** Returns the commit that branch points to if it exists. Returns null otherwise */
    public Commit getBranchCommit(String branch) {
        if (branches.containsKey(branch)) {
            String branchHash = branches.get(branch);
            return Commit.fromFile(branchHash);
        }
        return null;
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
