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
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.assertj.core.util.Files;
import org.dspace.AbstractUnitTest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.junit.Test;

/**
 * Tests for {@link ItemPacker}
 *
 * @author mikejritter
 */
public class ItemPackerTest extends AbstractUnitTest {

    private static final String BUNDLE_NAME = "bundle";

    private ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    private CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    private CollectionService collectionService = ContentServiceFactory.getInstance().getCollectionService();
    private WorkspaceItemService workspaceItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();
    private InstallItemService installItemService = ContentServiceFactory.getInstance().getInstallItemService();
    private EPersonService ePersonService = EPersonServiceFactory.getInstance().getEPersonService();

    @Test
    public void testPack() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson = ePersonService.findByEmail(context, "test@email.com");
        assertThat(ePerson).isNotNull();
        context.setCurrentUser(ePerson);

        // setup output
        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("");
        assertNotNull(resources);
        final Path output = Paths.get(resources.toURI().resolve("item-packer-test"));

        // Item entity
        // todo add a linked collection to the item
        final Item item = createItem(context);
        final File testBitstream = new File(testProps.getProperty("test.bitstream"));
        itemService.createSingleBitstream(context, new FileInputStream(testBitstream), item, BUNDLE_NAME);

        final ItemPacker packer = new ItemPacker(item, "zip");
        final File packedOutput = packer.pack(output.toFile());

        assertThat(packedOutput).exists();
        assertThat(packedOutput).isFile();
        packedOutput.delete();
        context.restoreAuthSystemState();
    }

    @Test
    public void testFetchThrowsException() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson = ePersonService.findByEmail(context, "test@email.com");
        assertThat(ePerson).isNotNull();
        context.setCurrentUser(ePerson);

        // setup output
        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("");
        assertNotNull(resources);
        final Path output = Paths.get(resources.toURI().resolve("item-packer-with-fetch"));

        // Item entity
        final Item item = createItem(context);
        final File testBitstream = new File(testProps.getProperty("test.bitstream"));
        itemService.createSingleBitstream(context, new FileInputStream(testBitstream), item, BUNDLE_NAME);

        // and perform the packaging
        final ItemPacker packer = new ItemPacker(item, "zip");
        packer.setReferenceFilter(BUNDLE_NAME + " 1 https://localhost/fetch");
        try {
            packer.pack(output.toFile());
        } catch (UnsupportedOperationException e) {
            assertNotNull(e);
        }

        Files.delete(output.toFile());
        context.restoreAuthSystemState();
    }

    @Test
    public void testUnpack() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson = ePersonService.findByEmail(context, "test@email.com");
        assertThat(ePerson).isNotNull();
        context.setCurrentUser(ePerson);

        final URL resources = CollectionPackerTest.class.getClassLoader().getResource("unpack");
        assertNotNull(resources);

        final Path archive = Paths.get(resources.toURI()).resolve("ITEM@123456789-3.zip");
        final Path openArchive = Paths.get(resources.toURI()).resolve("ITEM@123456789-3");

        // might be good to use the defined bundles/bitstreams from the setup
        final Item item = createItem(context);
        final ItemPacker packer = new ItemPacker(item, "zip");
        packer.unpack(archive.toFile());

        assertThat(openArchive).doesNotExist();
        context.restoreAuthSystemState();
    }

    public Item createItem(Context context) throws SQLException, AuthorizeException {
        final Community parentCommunity = communityService.create(null, context);
        final Collection collection = collectionService.create(context, parentCommunity);
        final WorkspaceItem workspaceItem = workspaceItemService.create(context, collection, false);
        return installItemService.installItem(context, workspaceItem);
    }

}
