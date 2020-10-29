/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.pack.bagit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.duraspace.bagit.BagConfig.SOURCE_ORGANIZATION_KEY;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.dspace.AbstractUnitTest;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.junit.Test;

/**
 * Tests to check the BagInfoHelper parses config values correctly
 *
 * @author mikejritter
 */
public class BagInfoHelperTest extends AbstractUnitTest {

    private static final String BAG_INFO = "bag-info.txt";
    private static final String OTHER_INFO = "other-info.txt";

    private ConfigurationService configurationService;

    @Test
    public void getTagFiles() {
        configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        final Map<String, Map<String, String>> tagFiles = BagInfoHelper.getTagFiles();

        assertThat(tagFiles).hasSize(2);
        assertThat(tagFiles).containsOnlyKeys(BAG_INFO, OTHER_INFO);
        assertThat(tagFiles).containsEntry(BAG_INFO, ImmutableMap.of(SOURCE_ORGANIZATION_KEY, SOURCE_ORG))
                            .containsEntry(OTHER_INFO, ImmutableMap.of("Misc", OTHER_INFO_MISC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidKeyLength() {
        configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        final String key = "replicate-bagit.tag.test.test-info.invalid-key";

        configurationService.setProperty(key, "");
        BagInfoHelper.getTagFiles();
    }
}