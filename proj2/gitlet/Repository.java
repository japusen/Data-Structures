package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

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
    public static final File GITLET_DIR = join(CWD, ".gitlet");

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
            //Initialize the branch and staging files
            Branch.BRANCH_FILE.createNewFile();
            Staging.STAGING_FILE.createNewFile();

            // Create the origin commit and save it to the commit folder
            Commit originCommit = new Commit("initial commit", "", new Date(0), null);
            String originCommitHash = sha1(originCommit);
            originCommit.saveCommit();

            // Create the MASTER branch and HEAD mappings to the origin commit
            Branch branch = new Branch();
            branch.updateMaster(originCommitHash);
            branch.updateHEAD(originCommitHash);
            branch.saveBranch();

            // Create an empty staging area and save it
            Staging stagingArea = new Staging();
            stagingArea.saveStaging();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Returns a mapping of the current directory files and the hash of their contents */
    public TreeMap<String, String> getCwdFiles() {
        TreeMap<String, String> fileMap = new TreeMap<>();

        List<String> file_names = plainFilenamesIn(CWD);
        if (file_names == null) {
            return fileMap;
        }

        for (String name : file_names) {
            File currentFile = Utils.join(CWD, name);
            byte[] contents = readContents(currentFile);
            String hash = sha1(contents);
            fileMap.put(name, hash);
        }

        return fileMap;
    }
}
