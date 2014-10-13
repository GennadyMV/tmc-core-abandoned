package fi.helsinki.cs.tmc.core.domain;

import fi.helsinki.cs.tmc.core.util.PathUtil;

import java.io.File;
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

        this(exercise, new ArrayList<String>());
    }

    public Project(final Exercise exercise, final List<String> projectFiles) {

        this.exercise = exercise;
        this.projectFiles = projectFiles;
        this.extraStudentFiles = Collections.emptyList();
        this.rootPath = buildRootPath();
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

    public ProjectType getProjectType() {

        ProjectType type;
        synchronized (projectFiles) {
            final List<String> files = new ArrayList<String>(projectFiles);
            type = ProjectType.findProjectType(files);
        }
        return type;
    }

    public boolean containsFile(final String file) {

        if (file == null || rootPath.isEmpty()) {
            return false;
        }
        return (file + "/").contains(rootPath + "/");
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

    private String buildRootPath() {

        final ProjectType type = getProjectType();
        if (type == null) {
            return "";
        }
        
        synchronized (projectFiles) {
            if (projectFiles.size() == 1) {
                final File projectFile = new File(projectFiles.get(0));
                if (projectFile.isDirectory()) {
                    return PathUtil.getUnixPath(projectFile.getAbsolutePath());
                } else {
                    return PathUtil.getUnixPath(projectFile.getParent());
                }
            }
            String shortest = null;
            for (final String file : projectFiles) {
                if (shortest == null || file.length() < shortest.length()) {
                    shortest = file;
                } else {
                    for (int i = 0; i < shortest.length(); i++) {
                        if (!(shortest.charAt(i) == file.charAt(i))) {
                            shortest = shortest.substring(0, i);
                        }
                    }
                }
            }
            if (shortest != null) {
                final File rootPathFile = new File(shortest);
                return PathUtil.getUnixPath(rootPathFile.getAbsolutePath());
            }
        }
        return "";
    }

    public void setProjectFiles(final List<String> files) {

        synchronized (projectFiles) {
            projectFiles = files;
            rootPath = buildRootPath();
        }
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

        synchronized (projectFiles) {
            projectFiles.add(file);
        }
    }

    public void removeProjectFile(final String path) {

        synchronized (projectFiles) {
            projectFiles.remove(path);
        }
    }

    public void setExtraStudentFiles(final List<String> files) {

        extraStudentFiles = files;
    }

    public List<String> getExtraStudentFiles() {

        return extraStudentFiles;
    }

    public boolean existsOnDisk() {

        synchronized (projectFiles) {

            for (final String file : projectFiles) {
                if (file.replace(getRootPath(), "").contains("/src/")) {
                    return true;
                }
            }
        }
        return false;
    }

    public ProjectStatus getStatus() {

        return status;
    }

    public void setStatus(final ProjectStatus status) {

        this.status = status;
    }

}
