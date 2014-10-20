package fi.helsinki.cs.tmc.client.core.clientspecific;

import fi.helsinki.cs.tmc.client.core.domain.Project;

public interface MavenRunner {

    int runGoal(String string, Project project);

}
