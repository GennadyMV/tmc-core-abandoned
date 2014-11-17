package fi.helsinki.cs.tmc.client.core.io.unzip.decider;

import fi.helsinki.cs.tmc.client.core.domain.Project;

public abstract class AbstractOverwritingDecider implements OverwritingDecider {

    private Project project;

    public AbstractOverwritingDecider(final Project project) {

        this.project = project;
    }

    @Override
    public boolean mayOverwrite(final String relativePath) {

        return mayModify(relativePath);
    }

    @Override
    public boolean mayDelete(final String relativePath) {

        return mayModify(relativePath);
    }

    private boolean mayModify(final String filePath) {

        for (String studentFile : project.getExtraStudentFiles()) {

            if (studentFile.endsWith("/") && filePath.startsWith(studentFile)) {
                return false;
            } else if (filePath.equals(studentFile)) {
                return false;
            }
        }

        return true;
    }

    protected Project getProject() {

        return project;
    }


//        for (String s : doNotUnzip) {
//            if (s.charAt(s.length() - 1) == '/') {
//                s = s.substring(0, s.length() - 1);
//            }
//
//            s = (project.getRootPath() + "/" + s);
//
//            if (filePath.startsWith(s) && (filePath.equals(s) || filePath.charAt(s.length()) == '/')) {
//                return false;
//            }
//        }
//        return true;
}
