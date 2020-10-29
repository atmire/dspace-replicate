/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.pack.bagit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.dspace.AbstractDSpaceTest;
import org.dspace.content.Community;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
import org.dspace.core.Context;
import org.dspace.curate.Curator;
import org.junit.Test;

/**
 * Tests for {@link CommunityPacker}
 *
 * @author mikejritter
 */
public class CommunityPackerTest extends AbstractDSpaceTest {

    final CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();

    @Test
    public void testPack() throws Exception {
        final Context context = Curator.curationContext();
        context.turnOffAuthorisationSystem();

        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("");
        assertNotNull(resources);
        final Path output = Paths.get(resources.toURI().resolve("community-packer-test"));

        final Community community = communityService.create(null, context);
        assertNotNull(community);
        assertNotNull(community.getID());

        final CommunityPacker packer = new CommunityPacker(community, "zip");
        final File packedOutput = packer.pack(output.toFile());

        assertThat(packedOutput).exists();
        assertThat(packedOutput).isFile();
        packedOutput.delete();
    }

    @Test
    public void testUnpack() throws Exception {
        final Context context = Curator.curationContext();
        context.turnOffAuthorisationSystem();

        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("unpack");
        assertNotNull(resources);

        final Path archive = Paths.get(resources.toURI()).resolve("COMMUNITY@123456789-1.zip");
        final Path openArchive = Paths.get(resources.toURI()).resolve("COMMUNITY@123456789-1");

        final Community community = communityService.create(null, context);
        assertNotNull(community);
        assertNotNull(community.getID());
        final UUID uuid = community.getID();

        final CommunityPacker packer = new CommunityPacker(community, "zip");
        packer.unpack(archive.toFile());

        final Community updated = communityService.find(context, uuid);
        assertThat(updated).isNotNull();
        assertThat(updated.getResourcePolicies())
            .isNotNull()
            .isEmpty();

        assertThat(openArchive).doesNotExist();
    }

}