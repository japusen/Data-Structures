package gitlet;

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
     * HEAD -- the name of the HEAD pointer
     * MASTER -- the name of the Master branch
     * branchMap -- Branches and their associated commit : Branch Name -> commitID
     * currentBranch -- The current branch being worked on
     *
     */

    static final File BRANCH_FILE = Utils.join(Repository.GITLET_DIR, "branches");
    static final String HEAD = "HEAD";
    static final String MASTER = "master";
    private TreeMap<String, String> branchMap;
    private String currentBranch;

    Branch(String commitID) {
        branchMap = new TreeMap<>();
        currentBranch = MASTER;
        updateBranch(commitID);
        updateHEAD(commitID);
        saveToFile();
    }

    /** Points the HEAD branch to the provided commit */
    public void updateHEAD(String commitID) {
        branchMap.put(HEAD, commitID);
    }

    /** Points the provided branch to the provided commit */
    public void updateBranch(String commitID) {
        branchMap.put(currentBranch, commitID);
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
        return branchMap.get(HEAD);
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

    @Override
    public void dump() {
        System.out.printf("Current Branch: %s%nAll Branches: %s%n", currentBranch, branchMap);
    }
}
