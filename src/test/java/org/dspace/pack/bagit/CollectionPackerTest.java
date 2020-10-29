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
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.core.Context;
import org.dspace.curate.Curator;
import org.junit.Test;

/**
 * Tests for {@link CollectionPacker}
 */
public class CollectionPackerTest extends AbstractDSpaceTest {

    final CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    final CollectionService collectionService = ContentServiceFactory.getInstance().getCollectionService();

    public Collection createCollection(final Context context) throws Exception {
        final Community community = communityService.create(null, context);
        return collectionService.create(context, community);
    }

    @Test
    public void testPack() throws Exception {
        final Context context = Curator.curationContext();
        context.turnOffAuthorisationSystem();

        // get the output location
        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("");
        assertNotNull(resources);
        final Path output = Paths.get(resources.toURI().resolve("collection-packer-test"));

        // init test Collection
        final Collection collection = createCollection(context);
        assertNotNull(collection);
        assertNotNull(collection.getID());
        assertNotNull(collection.getCommunities());

        final CollectionPacker collectionPacker = new CollectionPacker(collection, "zip");
        final File packedOutput = collectionPacker.pack(output.toFile());

        // todo: check packedOutput for correctness
        assertThat(packedOutput).exists();
        assertThat(packedOutput).isFile();
        packedOutput.delete();

        context.restoreAuthSystemState();
    }

    @Test
    public void testUnpack() throws Exception {
        final Context context = Curator.curationContext();
        context.turnOffAuthorisationSystem();
        // push to setup
        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("unpack");
        assertNotNull(resources);

        final Path archive = Paths.get(resources.toURI()).resolve("COLLECTION@123456789-2.zip");
        final Path openArchive = Paths.get(resources.toURI()).resolve("COLLECTION@123456789-2");

        final Collection collection = createCollection(context);
        assertNotNull(collection);
        assertNotNull(collection.getID());
        final UUID uuid = collection.getID();

        final CollectionPacker packer = new CollectionPacker(collection, "zip");
        packer.unpack(archive.toFile());

        final Collection updated = collectionService.find(context, uuid);
        assertThat(updated).isNotNull();
        assertThat(updated.getResourcePolicies())
            .isNotNull()
            .isEmpty();

        assertThat(openArchive).doesNotExist();

        context.restoreAuthSystemState();
    }

}