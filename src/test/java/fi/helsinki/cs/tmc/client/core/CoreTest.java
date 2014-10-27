package fi.helsinki.cs.tmc.client.core;

import fi.helsinki.cs.tmc.client.core.clientspecific.UIInvoker;
import fi.helsinki.cs.tmc.client.core.domain.Settings;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CoreTest {

    @Before
    public void setUp() {

        Core.initialize(null, null);
    }

    @Test
    public void initialisationSetsUIInvoker() {

        final UIInvoker invoker = mock(UIInvoker.class);

        Core.initialize(invoker, null);

        assertEquals(invoker, Core.getUIInvoker());
    }

    @Test
    public void initialisationSetsSettings() {

        final Settings settings = mock(Settings.class);

        Core.initialize(null, settings);

        assertEquals(settings, Core.getSettings());
    }

    @Test
    public void getExecutorServiceReturnsNonNullInstanceOfExecutorService() {

        assertNotNull(Core.getExecutorService());
        assertTrue(Core.getExecutorService() instanceof ExecutorService);
    }

    @Test(expected = IllegalAccessException.class)
    public void canNotConstruct() throws InstantiationException, IllegalAccessException {

        Core.class.newInstance();
        fail("Should have private constructor");
    }

    /*
     * Make sure Cobertura knows we visited the private constructor...
     */
    @Test
    public void coverageForPrivateConstructor() throws Exception {

        final Constructor<?>[] constructor = Core.class.getDeclaredConstructors();
        constructor[0].setAccessible(true);
        constructor[0].newInstance((Object[]) null);
    }

}
