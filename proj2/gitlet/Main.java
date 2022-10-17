package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Julius Apusen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                if (Repository.GITLET_DIR.exists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }
                Repository.initialize();
                break;
            case "add":
                validateNumArgs(args, 2);
                validateCWD();
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                validateCWD();
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                validateCWD();
                Repository.remove(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                validateCWD();
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                validateCWD();
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                validateCWD();
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                validateCWD();
                Repository.status();
                break;
            case "checkout":
                validateCWD();
                if (args.length == 2) {
                    String branchName = args[1];
                    break;
                } else if (args.length == 3) {
                    String fileName = args[2];
                    break;
                } else if (args.length == 4) {
                    String commitID = args[1];
                    String fileName = args[3];
                    break;
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "branch":
                validateCWD();
                validateNumArgs(args, 2);
                Repository.branch(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    public static void validateCWD() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
