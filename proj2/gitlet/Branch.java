package gitlet;

import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

/** Represents the gitlet repository branches
 *  Maintains the state of the branches and the HEAD pointer in the repository
 *
 *  @author Julius Apusen
 */
public class Branch implements Serializable, Dumpable {
    /**
     * BRANCH_FILE -- File that contains the serialized objects
     * head -- the current branch
     * MASTER -- the name of the Master branch
     * branchMap -- Branches and their associated commit : Branch Name -> commitID
     * currentBranch -- The current branch being worked on
     *
     */

    static final File BRANCH_FILE = Utils.join(Repository.GITLET_DIR, "branches");
    static final String MASTER = "master";
    private TreeMap<String, String> branchMap;
    private String head;

    Branch(String commitID) {
        branchMap = new TreeMap<>();
        head = MASTER;
        updateBranch(commitID);
        saveToFile();
    }

    /** Points the HEAD branch to the provided branch */
    public void changeHEAD(String branch) {
        head = branch;
    }

    /** Points the provided branch to the provided commit */
    public void updateBranch(String commitID) {
        branchMap.put(head, commitID);
    }

    /** Adds a new branch with the corresponding commit */
    public void newBranch(String branch, String commitID) {
        branchMap.put(branch, commitID);
    }

    /** Returns true if branch already exists in branches */
    public boolean containsBranch(String branchName) {
        return branchMap.containsKey(branchName);
    }

    /** Returns the commit that HEAD points to */
    public String getHEADCommitID() {
        return branchMap.get(head);
    }

    /** Returns the commit that branch points to if it exists. Returns null otherwise */
    public Commit getBranchCommitID(String branchName) {
        if (branchMap.containsKey(branchName)) {
            String branchCommitID = branchMap.get(branchName);
            return Repository.loadCommitFromFile(branchCommitID);
        }
        return null;
    }

    /** Saves the state of the branch to the file */
    public void saveToFile() {
        File file = Utils.join(Repository.GITLET_DIR, "branches");
        Utils.writeObject(file, this);
    }

    /** Print the branches during status command */
    public void printBranches(String headCommitID) {
        System.out.println("=== Branches ===");
        for (String key: branchMap.keySet()) {
            if (key.equals(head)) {
                System.out.println("*" + key);
            } else {
                System.out.println(key);
            }
        }
        System.out.println();
    }

    @Override
    public void dump() {
        System.out.printf("Current Branch: %s%nAll Branches: %s%n", head, branchMap);
    }
}
