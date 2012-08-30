//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FilenameFilter;
//import java.io.IOException;
/**
 * Our exception class to handle premature eond of file conditions. This is
 * passed up from the read class to the calling methods. Essentially this is
 * just a renamed IOException that occurs under known cirumstances.
 * 
 * @author Lewis A. Sellers (min)
 * @returns nothing
 **/
@SuppressWarnings("serial")
class EOFException extends Exception {
	EOFException() {
		super();
		//
	}

	EOFException(String errMsg) {
		super(errMsg);
		//
	}
}