package version2;

/**
 * Custom exception to be thrown when data is expected but a csv file is blank.
 */
public class NoDataFoundException extends Exception {

    public NoDataFoundException(String errorMessage) {
        super(errorMessage);
    }

}
