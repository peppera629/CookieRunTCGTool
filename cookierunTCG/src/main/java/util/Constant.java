package util;

import java.io.File;
import java.util.Scanner;

public class Constant {
	public static String VERSION = "0.21.0";
	public static String DATA_VERSION = "Unknown";

	static {

		// Data version: Struct.Major.Minor.Patch - Data structure change, new pack, new individual cards, fixes and additions (including internal ones)
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
