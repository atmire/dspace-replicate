/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.pack.bagit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.UUID;

import org.dspace.AbstractUnitTest;
import org.dspace.content.DSpaceObject;
import org.dspace.core.ReloadableEntity;

/**
 * Base class for all BagIt packing/unpacking tests. This performs initial setup so that the DSpaceKernel is not null
 * and so that some of the that are used through static contexts or have static initializers (e.g.
 * {@link org.dspace.services.factory.DSpaceServicesFactory}, {@link org.dspace.core.Context}) can initialize and
 * retrieve any classes which are necessary for basic operations.
 *
 * @author mikejritter
 */
public abstract class BagItPackerTest extends AbstractUnitTest {

    // public static final String EVENT_SERVICE_FACTORY = "eventServiceFactory";
    // public static final String EPERSON_SERVICE_FACTORY = "ePersonServiceFactory";
    // public static final String AUTHORIZE_SERVICE_FACTORY = "authorizeServiceFactory";
    // Mocks for Context init
    // private final DBConnection dbConnection = mock(DBConnection.class);
    // private final EventService eventService = mock(EventService.class);
    // private final EventServiceFactory eventServiceFactory = mock(EventServiceFactory.class);

    protected final String archFmt = "zip";

    public BagItPackerTest() {
        super();
    }

    /**
    @Before
    @Override
    public void init() {
        super.init();
    }

    @After
    @Override
    public void destroy() {
        super.destroy();
    }
    */

    /*
    @After
    public void verifyMocks() {
        verify(eventServiceFactory, atLeastOnce()).getEventService();
    }
     */

    /**
     * Initialize a DSpaceObject JPA entity with a random UUID
     *
     * @param clazz the {@link Class} to initialize
     * @param <T> the type of the class
     * @return the initialized object
     * @throws ReflectiveOperationException when the class cannot be instantiated
     */
    protected <T extends DSpaceObject> T initDSO(Class<T> clazz) throws ReflectiveOperationException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T t = constructor.newInstance((Object []) null);
        Field id = DSpaceObject.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(t, UUID.randomUUID());
        return t;
    }

    /**
     * Initialize a {@link ReloadableEntity<Integer>} and set the id to 1
     *
     * @param clazz the class to initialize
     * @param <T> the type of the class
     * @return the initialized object
     * @throws ReflectiveOperationException when the class cannot be instantiated
     */
    protected <T extends ReloadableEntity<Integer>> T initReloadable(Class<T> clazz)
        throws ReflectiveOperationException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T t = constructor.newInstance((Object []) null);
        Field id = clazz.getDeclaredField("id");
        id.setAccessible(true);
        id.set(t, 1);
        return t;
    }

}
