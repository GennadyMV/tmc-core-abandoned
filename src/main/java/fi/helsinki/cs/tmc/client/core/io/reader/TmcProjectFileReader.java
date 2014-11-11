package fi.helsinki.cs.tmc.client.core.io.reader;

import fi.helsinki.cs.tmc.client.core.domain.TmcProjectFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.yaml.snakeyaml.Yaml;

public class TmcProjectFileReader {

    private static final Logger LOG = LogManager.getLogger();

    public TmcProjectFile read(final File file) {

        final TmcProjectFile projectFile = new TmcProjectFile();

        if (!file.exists()) {
            return projectFile;
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException("Provided file is a directory: " + file.getAbsolutePath());
        }


        try (Reader reader = new BufferedReader(new FileReader(file))) {

            final Object root = new Yaml().load(reader);
            final List<String> extraStudentFiles = parse(root);

            if (extraStudentFiles != null) {
                projectFile.setExtraStudentFiles(extraStudentFiles);
            }

        } catch (final IOException exception) {
            LOG.warn("Failed to read " + file.getPath(), exception);
        }

        return projectFile;
    }

    private List<String> parse(final Object root) {

        if (!(root instanceof Map)) {
            return null;
        }

        final Map<?, ?> rootMap = (Map<?, ?>) root;
        final Object files = rootMap.get("extra_student_files");

        if (files instanceof List) {

            final List<String> extraStudentFiles = new ArrayList<>();

            for (final Object value : (List<?>) files) {

                if (value instanceof String) {
                    extraStudentFiles.add((String) value);
                }
            }

            return extraStudentFiles;
        }

        return null;
    }

}
