package fi.helsinki.cs.tmc.client.core.http;

import java.net.ProxySelector;

import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Implementation of HttpClientFactory interface. 
 * Creates actual HttpClients that can be used to connect to the internet.
 */
public class HttpClientFactory {

    /**
     * Creates HTTP client.
     */
    public static CloseableHttpAsyncClient makeHttpClient() {
              
        final HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom().useSystemProperties()
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .setRedirectStrategy(new LaxRedirectStrategy());
        
        maybeSetProxy(httpClientBuilder);

        return httpClientBuilder.build();

    }

    /**
     * Sets builder to use system proxy if one is defined.
     */
    private static void maybeSetProxy(final HttpAsyncClientBuilder httpClientBuilder) {

        final SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        
        if (routePlanner != null) {
            httpClientBuilder.setRoutePlanner(routePlanner);
        }
    }
}
