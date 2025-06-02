package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientProviderTest {

    @Test
    void singletonInstanceIsNotNull() {
        HttpClient c1 = HttpClientProvider.instance();
        assertNotNull(c1);
        HttpClient c2 = HttpClientProvider.instance();
        assertSame(c1, c2); // singleton
    }
}
