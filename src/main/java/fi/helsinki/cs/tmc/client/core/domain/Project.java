package fi.helsinki.cs.tmc.client.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A domain class for IDE independent data storage of project data, such as it's
 * path, status and files. Similar to the Exercise class, but while Exercise
 * reflects server side information, the Project class tracks local information.
 */
public class Project {

    private Exercise exercise;
    private List<String> projectFiles;
    private List<String> extraStudentFiles;
    private String rootPath;
    private ProjectStatus status;

    public Project(final Exercise exercise) {

        this(exercise, "", new ArrayList<String>());
    }

    public Project(final Exercise exercise, final String rootPath, final List<String> projectFiles) {

        this.exercise = exercise;
        this.projectFiles = projectFiles;
        this.extraStudentFiles = Collections.emptyList();
        this.rootPath = rootPath;
        this.status = ProjectStatus.NOT_DOWNLOADED;

        exercise.setProject(this);
    }

    public Exercise getExercise() {

        return exercise;
    }

    public void setExercise(final Exercise exercise) {

        this.exercise = exercise;
    }

    public String getRootPath() {

        return rootPath;
    }

    public void setRootPath(final String rootPath) {

        this.rootPath = rootPath;
    }

    public ProjectType getProjectType() {

        return ProjectType.findProjectType(projectFiles);
    }

    public boolean containsFile(final String file) {

        if (file == null || rootPath.isEmpty()) {
            return false;
        }

        return (file + "/").contains(rootPath + "/");
    }

    public void setProjectFiles(final List<String> files) {

        projectFiles = files;
    }

    public List<String> getReadOnlyProjectFiles() {

        return Collections.unmodifiableList(projectFiles);
    }

    public String getProjectFileByName(final String fileName) {

        for (final String s : projectFiles) {
            if (s.contains(fileName) && !s.contains("/target/")) {
                return s;
            }
        }

        return "";
    }

    public void addProjectFile(final String file) {

        projectFiles.add(file);
    }

    public void removeProjectFile(final String path) {

        projectFiles.remove(path);
    }

    public void setExtraStudentFiles(final List<String> files) {

        extraStudentFiles = files;
    }

    public List<String> getExtraStudentFiles() {

        return extraStudentFiles;
    }

    public ProjectStatus getStatus() {

        return status;
    }

    public void setStatus(final ProjectStatus status) {

        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {

        if (o == null || !(o instanceof Project)) {
            return false;
        }

        final Project p = (Project) o;

        if (exercise == null || p.exercise == null) {
            return false;
        }

        return exercise.equals(p.exercise);
    }

    @Override
    public int hashCode() {

        return exercise.hashCode();
    }

}
