package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;


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
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    /** Folder that dogs live in. */
    static final File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commits");

    /** Folder that blobs live in. */
    static final File BLOB_DIR = Utils.join(Repository.GITLET_DIR, "blobs");

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
            Branch.BRANCH_FILE.createNewFile();
            Staging.STAGING_FILE.createNewFile();

            // Create the origin Commit obj and save it to COMMIT_DIR
            Commit originCommit = new Commit("initial commit", null, new Date(0), null);
            originCommit.saveCommitToDir();
            String originID = originCommit.getCommitID();


            // Create the MASTER branch and HEAD mappings to the origin commit
            Branch branches = new Branch();
            branches.updateMaster(originID);
            branches.updateHEAD(originID);
            branches.saveBranch();

            // Create an empty staging area and save it
            Staging stagingArea = new Staging();
            stagingArea.saveStagingArea(); // TODO fix how to save globally

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void add(String fileName) {
        File addedFile = Utils.join(Repository.CWD, fileName);
        // Make sure the file exists
        validateFile(addedFile);

        // Get the Staging Area and Branches
        Staging stagingArea = Staging.fromFile();
        Branch branches = Branch.fromFile();

        // Get the blob hash
        String newBlobID= getBlobID(addedFile);

        // Get the commit from the HEAD
        Commit headCommit = branches.getHEADCommit();

        // Check for the file in the commit, if it exists compare it to the blob of the fileName
        if (headCommit.containsFile(fileName)) {
            String headBlobID = headCommit.getBlobID(fileName);

            // If the blobs are the same, cancel the add if it's already staged
            // Otherwise, stage it for adding and cancel removal if it was staged
            if (sameBlobID(newBlobID, headBlobID)) {
                stagingArea.cancelAdd(fileName);
                stagingArea.saveStagingArea();

            } else {
                stagingArea.stageAdd(fileName, newBlobID);
                stagingArea.cancelRemove(fileName);
                stagingArea.saveStagingArea();
                // Create the blob file
                saveNewBlob(newBlobID, Utils.readContents(addedFile));
            }
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
    public static String getBlobID(File file) {
        byte[] fileContents = Utils.readContents(file);
        return Utils.sha1(fileContents);
    }

    /** Returns true if the blobs are the same */
    public static boolean sameBlobID(String id1, String id2) {
        return id1.equals(id2);
    }

    /** Creates a new blob file in the BLOB_DIR */
    public static void saveNewBlob(String blobID, byte[] contents) {
        File blobFile = Utils.join(BLOB_DIR, blobID);
        try {
            blobFile.createNewFile();
            Utils.writeContents(blobFile, contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
