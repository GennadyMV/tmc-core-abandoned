package fi.helsinki.cs.tmc.core.io;

import fi.helsinki.cs.tmc.core.domain.TmcProjectFile;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

public class TmcProjectFileReader {
    
    private static final Logger LOG = Logger.getLogger(TmcProjectFile.class.getName());
    
    public static TmcProjectFile read(final FileIO file) {
        
        final TmcProjectFile projectFile = new TmcProjectFile();
        
        load(projectFile, file);
        
        return projectFile;
    }

    private static void load(final TmcProjectFile projectFile, final FileIO file) {

        if (!file.fileExists()) {
            return;
        }
        
        try {
            
            final Reader reader = file.getReader();
            try {
                
                final Object root = new Yaml().load(reader);
                final List<String> extraStudentFiles = parse(root);
                
                if (extraStudentFiles != null) {
                    projectFile.setExtraStudentFiles(extraStudentFiles);
                }
                
            } finally {
                reader.close();
            }
        } catch (final IOException e) {
            LOG.log(Level.WARNING, "Failed to read {0}: {1}", new Object[] { file.getPath(), e.getMessage() });
        }
    }

    private static List<String> parse(final Object root) {

        if (!(root instanceof Map)) {
            return null;
        }
        
        final Map<?, ?> rootMap = (Map<?, ?>) root;
        final Object files = rootMap.get("extra_student_files");
        
        if (files instanceof List) {
            
            final List<String> extraStudentFiles = new ArrayList<String>();
            
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
