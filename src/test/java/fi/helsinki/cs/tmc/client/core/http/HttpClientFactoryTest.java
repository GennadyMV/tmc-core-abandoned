package fi.helsinki.cs.tmc.client.core.http;

import org.junit.Test;

import static org.junit.Assert.*;


public class HttpClientFactoryTest {

    @Test
    public void returnsClient() {

        assertNotNull(HttpClientFactory.makeHttpClient());
    }
}
