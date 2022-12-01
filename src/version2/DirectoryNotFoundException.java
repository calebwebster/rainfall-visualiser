package version2;

/**
 * Custom exception to be thrown when a directory is not found.
 */
public class DirectoryNotFoundException extends Exception {

    public DirectoryNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
