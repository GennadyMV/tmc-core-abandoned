package fi.helsinki.cs.tmc.client.core.domain;

import fi.helsinki.cs.tmc.client.core.io.unzip.decider.NeverOverwritingDecider;
import fi.helsinki.cs.tmc.client.core.io.unzip.decider.OverwritingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.DefaultZippingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.MavenZippingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZipEverythingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZippingDecider;

import java.util.List;

/**
 * An enumeration class for representing the project type.
 */
public enum ProjectType {

    JAVA_ANT("build.xml",
              DefaultZippingDecider.class,
              NeverOverwritingDecider.class),

    JAVA_MAVEN("pom.xml",
                MavenZippingDecider.class,
                NeverOverwritingDecider.class),

    MAKEFILE("Makefile",
              DefaultZippingDecider.class,
              NeverOverwritingDecider.class),

    NONE("\0",
          ZipEverythingDecider.class,
          NeverOverwritingDecider.class);

    private final Class<? extends ZippingDecider> zippingDecider;
    private final Class<? extends OverwritingDecider> overwritingDecider;
    private final String buildFile;

    private ProjectType(final String buildFile,
                         final Class<? extends ZippingDecider> zippingDecider,
                         final Class<? extends OverwritingDecider> overwritingDecider) {

        this.buildFile = buildFile;
        this.zippingDecider = zippingDecider;
        this.overwritingDecider = overwritingDecider;
    }

    public String getBuildFile() {

        return buildFile;
    }

    /**
     * A method that determines the project type by looking at the project files
     * and checking if files used by the build tool such as pom.xml or Makefile
     * are present.
     *
     * @param fileList
     *            list of project files
     * @return project type or none if it could not be determined
     */
    public static ProjectType findProjectType(final List<String> fileList) {

        for (final String file : fileList) {
            for (final ProjectType type : ProjectType.values()) {
                if (file.toLowerCase().endsWith(type.getBuildFile().toLowerCase())) {
                    return type;
                }
            }
        }

        return NONE;
    }

    public ZippingDecider getZippingDecider() throws InstantiationException, IllegalAccessException {

        return zippingDecider.newInstance();
    }

    public OverwritingDecider getOverwritingDecider() throws InstantiationException, IllegalAccessException {

        return overwritingDecider.newInstance();
    }
}
