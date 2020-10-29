package org.dspace.scripts.impl;

import org.apache.commons.cli.ParseException;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.scripts.MockDSpaceRunnableScriptConfiguration;
import org.dspace.utils.DSpace;

public class MockDSpaceRunnableScript extends DSpaceRunnable<MockDSpaceRunnableScriptConfiguration> {
    @Override
    public void internalRun() throws Exception {
    }

    @Override
    public MockDSpaceRunnableScriptConfiguration getScriptConfiguration() {
        return new DSpace().getServiceManager()
                           .getServiceByName("mock-script", MockDSpaceRunnableScriptConfiguration.class);
    }

    @Override
    public void setup() throws ParseException {
        if (!commandLine.hasOption("i")) {
            throw new ParseException("-i is a mandatory parameter");
        }
    }
}
