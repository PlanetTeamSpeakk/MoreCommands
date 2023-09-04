import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {
    // From https://stackoverflow.com/a/32052016/10099540
    static void pack(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath));
             Stream<Path> paths = Files.walk(sourceDirPath)) {
            for (Iterator<Path> it = paths.iterator(); it.hasNext();) {
                Path path = it.next();
                if (Files.isDirectory(path)) continue;

                ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                zs.putNextEntry(zipEntry);
                Files.copy(path, zs);
                zs.closeEntry();
            }
        }
    }
}
