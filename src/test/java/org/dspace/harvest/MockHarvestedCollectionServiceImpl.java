package org.dspace.harvest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockHarvestedCollectionServiceImpl extends HarvestedCollectionServiceImpl {

    @Override
    public List<String> verifyOAIharvester(String oaiSource,
                                           String oaiSetId, String metaPrefix, boolean testORE) {

        if (metaPrefix.equals("dc")) {
            return new ArrayList<>();
        } else {
            return Arrays.asList("(Mock error) Incorrect metadataConfigID");
        }
    }
}
