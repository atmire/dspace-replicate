/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.pack.bagit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.dspace.AbstractUnitTest;
import org.dspace.content.Bitstream;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.dspace.curate.Curator;
import org.dspace.pack.PackerFactory;
import org.dspace.pack.bagit.xml.metadata.Metadata;
import org.dspace.pack.bagit.xml.metadata.Value;
import org.dspace.pack.bagit.xml.policy.Policies;
import org.dspace.pack.bagit.xml.policy.Policy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mikejritter
 * @since 2020-03-09
 */
public class BagItAipWriterTest extends AbstractUnitTest {

    private final String archFmt = "zip";
    private final String objectType = "test-bag";
    private final String bundleName = "test-bundle";
    private final String xmlBody = "test-xml-body";
    private final String xmlAttr = "test-xml-attr";

    private Policies policies;
    private Metadata metadata;
    private List<BagBitstream> bitstreams;
    private Map<String, List<String>> properties;

    private BitstreamService bitstreamService;

    @Before
    public void setup() {
        final String objectTypeLine = PackerFactory.OBJECT_TYPE + objectType;
        properties = ImmutableMap.of(PackerFactory.OBJFILE, Collections.singletonList(objectTypeLine));
        metadata = new Metadata();
        metadata.addValue(new Value(xmlBody, xmlAttr));

        policies = new Policies();
        Policy policy = new Policy();
        policy.setType("READ");
        policies.addPolicy(policy);

        bitstreams = new ArrayList<>();

        bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();
    }

    @After
    public void clean() throws Exception {
        final String bagName = "test-write-aip.zip";
        final URL resources = this.getClass().getClassLoader().getResource("");
        final Path root = Paths.get(Objects.requireNonNull(resources).toURI());
        final Path createdBag = root.resolve(bagName);
        if (Files.exists(createdBag)) {
            Files.delete(createdBag);
        }
    }

    @Test
    public void testWriteAip() throws Exception {
        final Context context = Curator.curationContext();
        context.turnOffAuthorisationSystem();
        final String bagName = "test-write-aip";
        final URL resources = this.getClass().getClassLoader().getResource("");
        final Path root = Paths.get(Objects.requireNonNull(resources).toURI());
        final File directory = root.resolve(bagName).toFile();
        final Bitstream logo = bitstreamService.create(context, Files.newInputStream(root.resolve("existing-bagit-aip/data/policy.xml")));
        bitstreams.add(new BagBitstream(logo, bundleName, null, null));

        final BagItAipWriter writer = new BagItAipWriter(directory, archFmt, properties)
            .withLogo(logo)
            .withMetadata(metadata)
            .withPolicies(policies)
            .withBitstreams(bitstreams);

        final File packagedAip = writer.packageAip();

        assertThat(packagedAip).exists();
        assertThat(packagedAip).isFile();

        // read the manifests to get the entries written to the bag
        final Map<String, List<String>> contents = new HashMap<>();
        try (InputStream is = Files.newInputStream(packagedAip.toPath());
             ZipArchiveInputStream zis = new ZipArchiveInputStream(is)) {
            ArchiveEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final String entryName = entry.getName();
                if (entryName.endsWith("manifest-md5.txt")) {
                    final List<String> lines = IOUtils.readLines(zis);
                    contents.put(entryName, lines);
                }
            }
        }

        final String manifestKey = "test-write-aip/manifest-md5.txt";
        final String tagManifestKey = "test-write-aip/tagmanifest-md5.txt";
        assertThat(contents).containsKeys(manifestKey, tagManifestKey);

        // it would be nice to test the file names in the captured lines as well
        final List<String> manifestLines = contents.get(manifestKey);
        final List<String> tagManifestLines = contents.get(tagManifestKey);
        assertThat(manifestLines).hasSize(5);
        assertThat(tagManifestLines).hasSize(3);
    }

    @Test(expected = IllegalStateException.class)
    public void testWriteAipExists() throws Exception {
        final String bagName = "existing-bagit-aip";
        final URL resources = this.getClass().getClassLoader().getResource("");
        final Path root = Paths.get(Objects.requireNonNull(resources).toURI());

        final Bitstream logo = null;
        final File directory = root.resolve(bagName).toFile();

        final BagItAipWriter writer = new BagItAipWriter(directory, archFmt, properties)
            .withLogo(logo)
            .withMetadata(metadata)
            .withBitstreams(bitstreams);
        writer.packageAip();
    }

}