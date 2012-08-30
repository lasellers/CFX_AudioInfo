import java.io.File;
import java.io.FilenameFilter;

/**
     *
     *
     *
     **/
public class SingleFilenameFilter implements FilenameFilter {
	public SingleFilenameFilter(String filter) {
		this.filter = filter;
	}

	private String filter;

	public boolean accept(File dir, String name) {
		if (name.endsWith(filter))
			return true;

		return false; // (new File(dir, name)).isDirectory();
	}
}
