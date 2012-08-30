public class log {
	/**
	 * Error string for logging missing files or files unaccessable because of
	 * file security.
	 */
	private static String strError = "";

	/**
	 * Numeric error types.
	 **/
	final static public int iFILEDOESNOTEXIST = 0;
	final static public int iSECURITY = 1;
	final static public int iCORRUPT = 2;
	final static public int iGRAMMAR = 3;
	final static public int iUNSUPPORTEDFORMAT = 4;
	final static public int iUNSUPPORTEDSUBFORMAT = 5;
	final static public int iENDOFFILE = 7;

	//
	public static String get_error() {
		return strError;
	}

	/**
	 * 
	 * 
	 * 
	 * Logs general errors which happen before decoding of a specific file
	 * occurs. In general, this is used only to report iFILEDOESNOTEXIST or
	 * iSECURITY type errors.
	 * 
	 * The error is returned in the ColdFusion error variable, separate from the
	 * returned query data itself.
	 * 
	 * @parm err Numeric error type code such as iFILEDOESNOTEXIST, etc.
	 * @parm error Text string giving detailed information on the error.
	 * @return void
	 */
	public static void log_error(int err, String error) {
		strError += format_error(err, error);
	}

	
	public static String format_error(int err, String error) {
		switch (err) {
		case iFILEDOESNOTEXIST:
			return "[FILEDOESNOTEXIST]" + error;
		case iSECURITY:
			return "[SECURITY]" + error;
		case iCORRUPT:
			return "[CORRUPT]" + error;
		case iGRAMMAR:
			return "[GRAMMAR]" + error;
		case iUNSUPPORTEDFORMAT:
			return "[UNSUPPORTEDFORMAT]" + error;
		case iUNSUPPORTEDSUBFORMAT:
			return "[UNSUPPORTEDSUBFORMAT]" + error;
		case iENDOFFILE:
			return "[ENDOFFILE]" + error;
		}
		return "";
	}

}