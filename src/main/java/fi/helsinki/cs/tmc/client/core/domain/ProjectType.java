package fi.helsinki.cs.tmc.client.core.domain;

import fi.helsinki.cs.tmc.client.core.io.unzip.decider.DefaultUnzippingDecider;
import fi.helsinki.cs.tmc.client.core.io.unzip.decider.MavenUnzippingDecider;
import fi.helsinki.cs.tmc.client.core.io.unzip.decider.UnzipAllTheThings;
import fi.helsinki.cs.tmc.client.core.io.unzip.decider.UnzippingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.DefaultZippingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.MavenZippingDecider;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZipAllTheThings;
import fi.helsinki.cs.tmc.client.core.io.zip.decider.ZippingDecider;

import java.util.List;

/**
 * An enumeration class for representing the project type.
 */
public enum ProjectType {

    JAVA_ANT("build.xml", DefaultZippingDecider.class, DefaultUnzippingDecider.class), 
    JAVA_MAVEN("pom.xml", MavenZippingDecider.class, MavenUnzippingDecider.class), 
    MAKEFILE("Makefile", DefaultZippingDecider.class, DefaultUnzippingDecider.class),
    NONE("\0", ZipAllTheThings.class, UnzipAllTheThings.class);

    private final String buildFile;
    private final Class<? extends ZippingDecider> zippingDeciderClass;
    private final Class<? extends UnzippingDecider> unzippingDeciderClass;

    private ProjectType(final String buildFile,
                        final Class<? extends ZippingDecider> zippingDeciderClass,
                        final Class<? extends UnzippingDecider> unzippingDeciderClass) {

        this.buildFile = buildFile;
        this.zippingDeciderClass = zippingDeciderClass;
        this.unzippingDeciderClass = unzippingDeciderClass;
    }

    public String getBuildFile() {

        return buildFile;
    }
    
    public ZippingDecider getZippingDecider() throws InstantiationException, IllegalAccessException {
        
        return zippingDeciderClass.newInstance();
    }
    
    public UnzippingDecider getUnzippingDecider() throws InstantiationException, IllegalAccessException {
        
        return unzippingDeciderClass.newInstance();
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

}
