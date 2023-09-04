import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

// Forgix messes up the refmaps of client mixin configs, so we fix em here.
// Forgix renames them to 'client-fabric-MoreCommands...' in the mixin config while
// the file itself gets renamed to 'fabric-client-MoreCommands...' for whatever reason.
public class MergedJarRefmapFixTask extends DefaultTask {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final RegularFileProperty inputJar;

    public MergedJarRefmapFixTask() {
        ObjectFactory factory = getProject().getObjects();
        inputJar = factory.fileProperty();
    }

    @InputFile
    public RegularFileProperty getInputJar() {
        return inputJar;
    }

    public void setInputJar(RegularFile file) {
        inputJar.set(file);
    }

    public void setInputJar(File file) {
        inputJar.set(file);
    }

    @TaskAction
    void fixMergedJarRefmaps() {
        try {
            File tempDir = new File(getProject().getBuildDir(), "tmp/" + getName());
            FileUtils.deleteDirectory(tempDir); // Ensure it is empty
            tempDir.mkdirs();

            // Unzip input JAR
            getProject().copy(spec -> {
                spec.from(getProject().zipTree(inputJar));
                spec.into(tempDir);
            });

            boolean changed = false;
            try (Stream<Path> pathStream = Files.walk(tempDir.toPath(), 1)) {
                for (Iterator<Path> it = pathStream.iterator(); it.hasNext();)
                    changed |= processFile(it.next());
            }

            if (!changed) return;

            Utils.pack(tempDir.toPath(), inputJar.get().getAsFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error processing JAR file " + inputJar.get().getAsFile(), e);
        }
    }

    private boolean processFile(Path file) throws IOException {
        if (!Files.isRegularFile(file) || !file.getFileName().toString().endsWith(".mixins.json") ||
            !file.getFileName().toString().contains("client")) return false;

        JsonObject config;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            config = gson.fromJson(reader, JsonObject.class);
        }

        JsonElement refmapElement = config.get("refmap");
        if (refmapElement == null) throw new RuntimeException("Mixin config " + file.getFileName() + " has no refmap");

        String refmap = refmapElement.getAsString();
        if (!refmap.startsWith("client-")) return false;

        String[] parts = refmap.split("-", 3);
        refmap = String.join("-", parts[1], parts[0], parts[2]); // Swap first two parts
        // client-fabric-... becomes fabric-client-...

        config.remove("refmap");
        config.addProperty("refmap", refmap);

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            gson.toJson(config, writer);
        }
        return true;
    }
}
