package util;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Constant {
	public static String VERSION = "0.24.0";
	public static String DATA_VERSION = "Unknown";

	static {
		// final ResourceBundle constantsBundle = ResourceBundle.getBundle("constants", Locale.ROOT);
		// VERSION = constantsBundle.getString("version");

		// Data version: Major.Minor.Patch - New pack, new individual cards, fixes and additions (including internal ones)
		File ver_file = new File(AppPaths.dataDir().resolve("version.txt").toString());

		if (ver_file.exists()) {
			try (Scanner scanner = new Scanner(ver_file)) {
				if (scanner.hasNextLine()) {
					DATA_VERSION = scanner.nextLine().trim();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
