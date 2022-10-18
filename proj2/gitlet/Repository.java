package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * COMMIT_DIR -- File path to the hidden commits directory
     * BLOB_DIR -- File path to the hidden blob directory
     * dateFormat -- Format for printing out dates
     * originCommitID -- the ID of the origin commit
     */

    static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    static final File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commits");
    static final File BLOB_DIR = Utils.join(Repository.GITLET_DIR, "blobs");
    static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
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
            Commit originCommit = new Commit("initial commit", null,
                    DATE_FORMAT.format(new Date(0)), new TreeMap<>());
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
        String addedFileBlobID = calculateBlobID(addedFile);

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
        if (headCommit.isTracking(fileName)) {
            String headBlobID = headCommit.getBlobID(fileName);

            if (sameBlobID(addedFileBlobID, headBlobID)) {
                stagingArea.cancelAdd(fileName);
            } else {
                // Add and save blob to dir
                stagingArea.addFile(fileName, addedFileBlobID);
                saveBlobtoDir(addedFileBlobID, Utils.readContents(addedFile));
            }
        } else {
            // Add and save blob to dir
            stagingArea.addFile(fileName, addedFileBlobID);
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
        TreeMap<String, String> added = stagingArea.getAddedFiles();
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
        Commit newCommit = new Commit(message, prevCommitID,
                DATE_FORMAT.format(new Date()), commitFiles);
        String newCommitID = newCommit.getCommitID();

        // Update the current branch to the new commitID
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
        String removedFileBlobID = calculateBlobID(removedFile);

        // Load the Staging Area and Branches
        Staging stagingArea = loadStagingAreaFromFile();
        Branch branches = loadBranchesFromFile();

        // Load the commit from the HEAD
        Commit headCommit = loadCommitFromFile(branches.getHEADCommitID());

        // If the file is neither staged nor tracked by the head commit,
        // print the error message
        if (!stagingArea.hasAdded(fileName) && !headCommit.isTracking(fileName)) {
            System.out.println("No reason to remove the file.");
        } else {
            // Unstage the file if it is currently staged for addition
            stagingArea.cancelAdd(fileName);

            // If the file is tracked in the current commit, stage it for removal and
            // remove the file from the working directory if the user has not already done so
            if (headCommit.isTracking(fileName)) {
                stagingArea.removeFile(fileName, removedFileBlobID);
                Utils.restrictedDelete(removedFile);
            }

            // Save changes to staging area
            stagingArea.saveToFile();
        }
    }

    /** Prints the commits in the current branch */
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

    /** Prints all the commits that have been made */
    public static void globalLog() {
        List<String> commitFileNames = Utils.plainFilenamesIn(COMMIT_DIR);
        if (commitFileNames == null) {
            return;
        }
        for (String fileName : commitFileNames) {
            Commit commit = loadCommitFromFile(fileName);
            System.out.println(commit);
        }
    }

    /** Prints all the commits that have the message */
    public static void find(String message) {
        List<String> commitFileNames = Utils.plainFilenamesIn(COMMIT_DIR);
        int count = 0;
        if (commitFileNames == null) {
            return;
        }
        for (String fileName : commitFileNames) {
            Commit commit = loadCommitFromFile(fileName);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getCommitID());
                count++;
            }
        }
        if (count == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Prints out the status of the current working directory */
    public static void status() {
        // Load the Staging Area
        Staging stagingArea = loadStagingAreaFromFile();
        TreeMap<String, String> addedFiles = stagingArea.getAddedFiles();

        // Load the branches
        Branch branches = loadBranchesFromFile();

        // Load the commit from the HEAD
        String headCommitID = branches.getHEADCommitID();
        Commit headCommit = loadCommitFromFile(headCommitID);
        TreeMap<String, String> commitFiles = headCommit.getCommitFiles();

        // Print out Branches and Staging Area
        branches.printBranches(headCommitID);
        stagingArea.printFiles();

        // Load the files in the CWD
        TreeMap<String, String> cwdFiles = getCwdFiles();

        // Identify modified/deleted files in CWD that are not staged for commit
        TreeSet<String> modifiedFiles = new TreeSet<>();
        TreeSet<String> untrackedFiles = new TreeSet<>();

        // File is being tracked and either:
        // - in the cwd, but not staged for addition and does not have the same contents
        // - not in the cwd, but not staged for removal
        for (String fileName : commitFiles.keySet()) {
            String commitBlobID = commitFiles.get(fileName);
            if (cwdFiles.containsKey(fileName)) {
                String cwdBlobID = cwdFiles.get(fileName);
                if (!sameBlobID(commitBlobID, cwdBlobID) && !stagingArea.hasAdded(fileName)) {
                    modifiedFiles.add(fileName + " (modified)");
                }
            } else {
                if (!stagingArea.hasRemoved(fileName)) {
                    modifiedFiles.add(fileName + " (deleted)");
                }
            }
            cwdFiles.remove(fileName);
        }

        // File is staged for addition, but either:
        // - in the cwd, but the file contents are changed
        // - not in the cwd
        for (String fileName : stagingArea.getAddedFiles().keySet()) {
            String stagingBlobID = stagingArea.getAddedFiles().get(fileName);
            if (cwdFiles.containsKey(fileName)) {
                String cwdBlobID = cwdFiles.get(fileName);
                if (!sameBlobID(stagingBlobID, cwdBlobID)) {
                    modifiedFiles.add(fileName + " (modified)");
                }
            } else {
                modifiedFiles.add(fileName + " (deleted)");
            }
            cwdFiles.remove(fileName);
        }

        // Print modified files
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String fileName : modifiedFiles) {
            System.out.println(fileName);
        }
        System.out.println();

        // Anything left in cwdFiles is untracked
        System.out.println("=== Untracked Files ===");
        for (String fileName : cwdFiles.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    /** Creates a new branch at the HEAD */
    public static void branch(String branchName) {
        // Get the branches and head commitID
        Branch branches = loadBranchesFromFile();
        String headCommitID = branches.getHEADCommitID();

        // Add the branch if it doesn't already exist
        if (branches.containsBranch(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            branches.newBranch(branchName, headCommitID);
        }

        // Save changes to branches
        branches.saveToFile();
    }

    /** Removes a branch */
    public static void removeBranch(String branch) {
        // Load the branches
        Branch branches = loadBranchesFromFile();

        // Remove the branch if it exists
        if (!branches.containsBranch(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (branches.getHEAD().equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        } else {
            // Remove and save
            branches.remove(branch);
            branches.saveToFile();
        }
    }

    /** Checkout a single file from the commit id into the cwd */
    public static void checkout(String commitID, String fileName) {
        // Checkout on the head
        if (commitID.equals("head")) {
            commitID = loadBranchesFromFile().getHEADCommitID();
        }

        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        // The commit does not exist
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        // The commit does not track the file
        Commit checkoutCommit = loadCommitFromFile(commitID);
        if (!checkoutCommit.isTracking(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        // Write the file to the CWD
        writeFileToCWD(checkoutCommit, fileName);
    }

    /** Checkout all the files at the HEAD of a branch into the CWD */
    public static void checkout(String branch) {
        Branch branches = loadBranchesFromFile();

        // Branch does not exist
        if (!branches.containsBranch(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        // Branch is the current branch
        if (branches.getHEAD().equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        // Save the commitID of the current head
        String prevCommitID = branches.getHEADCommitID();

        // Change head to the new branch and get the new commit id
        branches.changeHEAD(branch);
        branches.saveToFile();

        // Overwrite
        String newCommitID = branches.getHEADCommitID();
        overwriteCWD(prevCommitID, newCommitID);
    }

    /** Reverts CWD to a specific commit given that the commit exists */
    public static void reset(String commitID) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        // The commit does not exist
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        // Load Staging Area and current Commit
        Branch branches = loadBranchesFromFile();
        String prevCommitID = branches.getHEADCommitID();

        // Update CWD
        overwriteCWD(prevCommitID, commitID);

        // Update head to commitID
        branches.updateBranch(commitID);
        branches.saveToFile();
    }

    /** Overwrites the CWD files to match the new commit */
    public static void overwriteCWD(String prevCommitID, String newCommitID) {
        // Load Staging Area and current Commit
        Staging stagingArea = loadStagingAreaFromFile();
        Commit prevCommit = loadCommitFromFile(prevCommitID);

        // There is an untracked file in the cwd
        Set<String> cwdFiles = getCwdFiles().keySet();
        for (String file : cwdFiles) {
            if (!stagingArea.hasAdded(file) && !prevCommit.isTracking(file)) {
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        // Current branch commit files
        Set<String> prevCommitFiles = prevCommit.getCommitFiles().keySet();

        // Get the checkout branch commit files
        Commit checkoutCommit = loadCommitFromFile(newCommitID);
        Set<String> checkoutCommitFiles = checkoutCommit.getCommitFiles().keySet();

        // Overwrite files of the new branch to the CWD
        for (String file: checkoutCommitFiles) {
            writeFileToCWD(checkoutCommit, file);
        }

        // Delete files in the CWD that were in the previous branch, but not in the checkout branch
        for (String file : prevCommitFiles) {
            if (!checkoutCommit.isTracking(file)) {
                Utils.restrictedDelete(Utils.join(CWD, file));
            }
        }

        // Clear the staging area
        stagingArea.clear();
        stagingArea.saveToFile();
    }

    /** Returns a mapping of the current directory files and the hash of their contents */
    public static TreeMap<String, String> getCwdFiles() {
        TreeMap<String, String> fileMap = new TreeMap<>();

        List<String> fileNames = Utils.plainFilenamesIn(CWD);
        if (fileNames == null) {
            return fileMap;
        }

        for (String blobID : fileNames) {
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
        return Utils.readObject(BRANCH_FILE, Branch.class);
    }

    /**
     * Reads in and deserializes a Commit from a file with given commitID in COMMIT_DIR.
     * @param commitID Name of commit to load
     * @return Commit read from file
     */
    public static Commit loadCommitFromFile(String commitID) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        return Utils.readObject(commitFile, Commit.class);
    }

    /** Returns the staging area from the staging file */
    public static Staging loadStagingAreaFromFile() {
        return Utils.readObject(STAGING_FILE, Staging.class);
    }

    /** Writes a file from a Commit to the CWD */
    public static void writeFileToCWD(Commit commit, String fileName) {
        // Get the file contents
        String fileBlobID = commit.getFileBlobID(fileName);
        File blobFile = Utils.join(Repository.BLOB_DIR, fileBlobID);
        byte[] contents = Utils.readContents(blobFile);

        // Write the file to the CWD
        File copy = Utils.join(CWD, fileName);
        Utils.writeContents(copy, contents);
    }

}
