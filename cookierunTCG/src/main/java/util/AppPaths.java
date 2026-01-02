package util;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;
import javax.swing.ImageIcon;

import app.AppAnchor;

public final class AppPaths {
    private AppPaths() {}

    public static Path appHome(Class<?> anchor) {
        try {
            Path location = Paths.get(anchor.getProtectionDomain().getCodeSource().getLocation().toURI());
            return Files.isDirectory(location) ? location : location.getParent();
        } catch (URISyntaxException e) {
            return Paths.get(System.getProperty("user.dir"));
        }
    }

    private static Path resolveDirNearAppOrCwd(String dirName) {
        // 1) Prefer cwd/<dirName> (VS Code / terminal runs)
        Path cwdCandidate = Paths.get(System.getProperty("user.dir")).resolve(dirName);
        if (Files.isDirectory(cwdCandidate)) return cwdCandidate;

        // 2) Next to jar/classes location
        Path home = appHome(AppAnchor.class);
        Path homeCandidate = home.resolve(dirName);
        if (Files.isDirectory(homeCandidate)) return homeCandidate;

        // 3) Dev fallback: if home == .../target/classes, try project root
        // home: <project>/target/classes -> root: <project>
        Path targetClasses = Paths.get("target", "classes");
        if (home.endsWith(targetClasses)) {
            Path root = home.getParent() != null ? home.getParent().getParent() : null;
            if (root != null) {
                Path rootCandidate = root.resolve(dirName);
                if (Files.isDirectory(rootCandidate)) return rootCandidate;
            }
        }

        // As a last resort, return cwdCandidate (even if missing) to keep behavior predictable
        return cwdCandidate;
    }

    public static Path appHome() {
        return appHome(AppAnchor.class);
    }

    public static Path dataDir() {
        return resolveDirNearAppOrCwd("data");
    }

    public static Path configDir() {
        return resolveDirNearAppOrCwd("config");
    }

    public static Path userDataDir() {
        return resolveDirNearAppOrCwd("userdata");
    }

    // For accessing classpath resources (data only)
    // in URL form (required by HTML)
    public static URL url(String path) {
        String p = path.startsWith("/") ? path.substring(1) : path;
        return AppAnchor.class.getClassLoader().getResource(p);
    }

    // in ImageIcon form (for Swing components)
    public static ImageIcon icon(String path) {
        URL u = url(path);
        return (u == null) ? new ImageIcon() : new ImageIcon(u);
    }

    public static String urlString(String path) {
        URL u = url(path);
        return (u == null) ? "" : u.toExternalForm();
    }
}