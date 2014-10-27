package fi.helsinki.cs.tmc.client.core.io.unzip.decider;


public class NeverOverwritingDecider implements OverwritingDecider {

    @Override
    public boolean mayOverwrite(final String relativePath) {

        return false;
    }

    @Override
    public boolean mayDelete(final String relativePath) {

        return false;
    }
}
