package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Branch.BRANCH_FILE;
import static gitlet.Staging.STAGING_FILE;


/** Represents a gitlet repository.
 * .gitlet/ -- top level folder for all persistent data in working directory
 *    - blobs/ -- folder containing all the persistent data for files
 *    - commits/ -- folder containing all the persistent data for commits
 *    - branches -- file containing the mapping of all branches and head to the associated commit
 *    - staging -- file to track files to be added or removed in a commit
 *  @author Julius Apusen
 */
public class Repository {
    /**
     * CWD -- File path to the current working directory
     * GITLET_DIR -- File path to the hidden .gitlet repository directory
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    /** Folder that dogs live in. */
    static final File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commits");

    /** Folder that blobs live in. */
    static final File BLOB_DIR = Utils.join(Repository.GITLET_DIR, "blobs");

    static String originCommitID;

    /**
     * Does the required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     */
    public static void initialize() {
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        try {
            //Create the branch file and staging area file
            BRANCH_FILE.createNewFile();
            STAGING_FILE.createNewFile();

            // Create the origin Commit obj and save it to COMMIT_DIR
            Commit originCommit = new Commit("initial commit", null, new Date(0), new TreeMap<>());
            originCommitID = originCommit.getCommitID();
            originCommit.saveToDir();

            // Create the MASTER branch and HEAD pointer. Point them to the origin commit
            Branch branches = new Branch(originCommitID);
            branches.saveToFile();

            // Create the staging area and save it
            Staging stagingArea = new Staging();
            stagingArea.saveToFile();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Updates the staging area for the addition of the file fileName*/
    public static void add(String fileName) {
        // Make sure the file exists
        File addedFile = Utils.join(Repository.CWD, fileName);
        validateFile(addedFile);
        String addedFileBlobID= calculateBlobID(addedFile);

        // Get the Staging Area and Branches
        Staging stagingArea = loadStagingAreaFromFile();
        Branch branches = loadBranchesFromFile();

        // Get the commit from the HEAD
        Commit headCommit = loadCommitFromFile(branches.getHEADCommitID());

        // Cancel potential queued removal
        stagingArea.cancelRemove(fileName);

        // If the file is already in the current commit,
        //      Compare blobIDs:
        //      If they are the same, remove it from staging area (file was reverted)
        //      If they are different, overwrite the addition
        // Otherwise, the file wasn't already in the commit, so add to staging area.
        if (headCommit.containsFile(fileName)) {
            String headBlobID = headCommit.getBlobID(fileName);

            if (sameBlobID(addedFileBlobID, headBlobID)) {
                stagingArea.cancelAdd(fileName);
            } else {
                // Add and save blob to dir
                stagingArea.stageAdd(fileName, addedFileBlobID);
                saveBlobtoDir(addedFileBlobID, Utils.readContents(addedFile));
            }
        } else {
            // Add and save blob to dir
            stagingArea.stageAdd(fileName, addedFileBlobID);
            saveBlobtoDir(addedFileBlobID, Utils.readContents(addedFile));
        }

        // Save changes to staging area
        stagingArea.saveToFile();
    }

    /** Creates a new commit and clears the staging area */
    public static void commit(String message) {
        // Message cannot be blank
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        // Load the Staging Area
        Staging stagingArea = loadStagingAreaFromFile();
        TreeMap<String, String> added = stagingArea.getAdd();
        TreeMap<String, String> removed = stagingArea.getRemove();

        // If no files have been staged, abort
        if (added.isEmpty() && removed.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // Load the branches
        Branch branches = loadBranchesFromFile();

        // Get the commit from the HEAD
        String prevCommitID = branches.getHEADCommitID();
        Commit prevCommit = loadCommitFromFile(prevCommitID);
        TreeMap<String, String> commitFiles = prevCommit.getCommitFiles();

        // Add files to commit
        for (String key : added.keySet()) {
            commitFiles.put(key, added.get(key));
        }

        // Remove files from commit
        for (String key : removed.keySet()) {
            commitFiles.remove(key);
        }

        // Create the New Commit
        Commit newCommit = new Commit(message, prevCommitID, new Date(), commitFiles);
        String newCommitID = newCommit.getCommitID();

        // Update the head and current branch to the new commitID
        branches.updateHEAD(newCommitID);
        branches.updateBranch(newCommitID);


        // Clear the staging area
        stagingArea.clear();

        // Save changes
        newCommit.saveToDir();
        branches.saveToFile();
        stagingArea.saveToFile();
    }

    /** If the file is being tracked, stage for removal and delete file from the CWD */
    public static void remove(String fileName) {
        // Make sure the file exists
        File removedFile = Utils.join(Repository.CWD, fileName);
        validateFile(removedFile);
        String removedFileBlobID= calculateBlobID(removedFile);

        // Get the Staging Area and Branches
        Staging stagingArea = loadStagingAreaFromFile();
        Branch branches = loadBranchesFromFile();

        // Get the commit from the HEAD
        Commit headCommit = loadCommitFromFile(branches.getHEADCommitID());

        // If the file is neither staged nor tracked by the head commit,
        // print the error message
        if (!stagingArea.containsFile(fileName) && !headCommit.containsFile(fileName)) {
            System.out.println("No reason to remove the file.");
        } else {
            // Unstage the file if it is currently staged for addition
            stagingArea.cancelAdd(fileName);

            // If the file is tracked in the current commit, stage it for removal and
            // remove the file from the working directory if the user has not already done so
            if (headCommit.containsFile(fileName)) {
                stagingArea.stageRemove(fileName, removedFileBlobID);
                Utils.restrictedDelete(removedFile);
            }

            // Save changes to staging area
            stagingArea.saveToFile();
        }
    }

    public static void log() {
        // Get Branches and Head Commit ID
        Branch branches = loadBranchesFromFile();
        String commitID = branches.getHEADCommitID();

        // Iterate through the parents until you reach the origin commit. Print each commit.
        while (commitID != null) {
            Commit commit = loadCommitFromFile(commitID);
            System.out.println(commit);
            commitID = commit.getParentID();
        }
    }

    /** Returns a mapping of the current directory files and the hash of their contents */
    public TreeMap<String, String> getCwdFiles() {
        TreeMap<String, String> fileMap = new TreeMap<>();

        List<String> file_names = Utils.plainFilenamesIn(CWD);
        if (file_names == null) {
            return fileMap;
        }

        for (String blobID : file_names) {
            File currentBlob = Utils.join(CWD, blobID);
            byte[] contents = Utils.readContents(currentBlob);
            String hash = Utils.sha1(contents);
            fileMap.put(blobID, hash);
        }

        return fileMap;
    }

    /** Checks if the file exists in the CWD.
     * Prints error message and exits the command if it does not */
    public static void validateFile(File file) {
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    /** Returns the blob ID of the given file */
    public static String calculateBlobID(File file) {
        byte[] fileContents = Utils.readContents(file);
        return Utils.sha1(fileContents);
    }

    /** Returns true if the blobs are the same */
    public static boolean sameBlobID(String id1, String id2) {
        return id1.equals(id2);
    }

    /** Creates a new blob file in the BLOB_DIR */
    public static void saveBlobtoDir(String blobID, byte[] contents) {
        File blobFile = Utils.join(BLOB_DIR, blobID);
        Utils.writeContents(blobFile, contents);
    }

    /** Returns the branch object from the branch file */
    public static Branch loadBranchesFromFile() {
        Branch branch = Utils.readObject(BRANCH_FILE, Branch.class);
        return branch;
    }

    /**
     * Reads in and deserializes a Commit from a file with given commitID in COMMIT_DIR.
     *
     * @param commitID Name of commit to load
     * @return Commit read from file
     */
    public static Commit loadCommitFromFile(String commitID) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }

    /** Returns the staging area from the staging file */
    public static Staging loadStagingAreaFromFile() {
        Staging stagingArea = Utils.readObject(STAGING_FILE, Staging.class);
        return stagingArea;
    }

}
