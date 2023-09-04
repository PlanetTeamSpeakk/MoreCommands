import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

// Fixes the refmaps of mixin configs.
// All compat projects have to be remapped *before* being included in the main jar,
// this means that they did not get transformed and thus did not get a refmap assigned.
// The refmap has been generated, but has not been referenced from the mixin configs, so we fix that here.
// There is also a separate issue caused by a bug in Architectury Transformer that causes
// the mixin configs from the client sourceset to reference the refmap of the main sourceset rather than
// its own refmap. This fixes that too.
public class RefmapFixTask extends DefaultTask {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final RegularFileProperty inputJar; // The jar to process

    public RefmapFixTask() {
        ObjectFactory factory = getProject().getObjects();
        inputJar = factory.fileProperty().convention(getProject().provider(() ->
                ((AbstractArchiveTask) getProject().getTasks().getByName("remapJar")).getArchiveFile().get()));
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
    void fixRefmap() {
        try {
            // Create a temporary directory to extract the JAR contents
            File tempDir = new File(getProject().getBuildDir(), "tmp/" + getName());
            FileUtils.deleteDirectory(tempDir); // Ensure it is empty
            tempDir.mkdirs();

            // Extract the JAR contents to the temporary directory
            getProject().copy(spec -> {
                spec.from(getProject().zipTree(inputJar));
                spec.into(tempDir);
            });

            String breadcrumbs = getBreadcrumbs();
            boolean changed = false;
            // Replace the content of each file with "hello world"
            try (Stream<Path> pathStream = Files.walk(tempDir.toPath(), 1)) {
                for (Iterator<Path> it = pathStream.iterator(); it.hasNext();)
                    changed |= processFile(breadcrumbs, it.next());
            }

            if (!changed) return;

            // Overwrite input
            Utils.pack(tempDir.toPath(), inputJar.get().getAsFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error processing JAR file " + inputJar.get().getAsFile(), e);
        }
    }

    private String getBreadcrumbs() {
        Project rootProject = getProject().getRootProject();

        List<String> breadcrumbs = new ArrayList<>();
        Project project = getProject();
        while (project != null && project != rootProject) {
            breadcrumbs.add(0, project.getName());
            project = project.getParent();
        }

        return rootProject.getName() + "-" + String.join("_", breadcrumbs);
    }

    private boolean processFile(String breadcrumbs, Path file) throws IOException {
        if (!Files.isRegularFile(file) || !file.toString().endsWith(".mixins.json")) return false;

        boolean client = file.getFileName().toString().contains("client");
        String refmap = (client ? "client-" : "") + breadcrumbs + "-refmap.json";

        JsonObject config;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            config = gson.fromJson(reader, JsonObject.class);
        }

        JsonElement refmapElement = config.get("refmap");
        if (refmapElement != null && refmap.equals(refmapElement.getAsString())) return false;

        config.remove("refmap");
        config.addProperty("refmap", refmap);

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            gson.toJson(config, writer);
        }
        return true;
    }
}
