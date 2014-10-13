package fi.helsinki.cs.tmc.core.domain;

import java.util.Collections;
import java.util.List;

/**
 * Represents the contents of a {@code .tmcproject.yml} file.
 */
public class TmcProjectFile {

    private List<String> extraStudentFiles;

    public TmcProjectFile() {

        extraStudentFiles = Collections.emptyList();
    }
    
    public TmcProjectFile(final List<String> extraStudentFiles) {
        
        this.extraStudentFiles = Collections.unmodifiableList(extraStudentFiles);
    }

    public List<String> getExtraStudentFiles() {

        return extraStudentFiles;
    }

    public void setExtraStudentFiles(final List<String> extraStudentFiles) {

        this.extraStudentFiles = Collections.unmodifiableList(extraStudentFiles);
    }
}
