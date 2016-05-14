package com.budjb.httprequests.examples

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.filter.bundled.AuthenticationTokenHeaderFilter

/**
 * This is an example implementation of the authentication token header filter.
 *
 * This class is meant to be a trivial example, and does a few things as shortcuts that applications should not do.
 * With API services, the authentication request is typically sent to an external service, whose address
 * is configured in the application.
 *
 * Additionally, request logging should never be enabled or should be properly masked when performing
 * HTTP requests that include sensitive data, such as authentication credentials. It is only present here
 * to demonstrate the steps the filter takes when handling authentication.
 */
class ExampleAuthenticationFilter extends AuthenticationTokenHeaderFilter {
    /**
     * HTTP client factory.
     */
    HttpClientFactory httpClientFactory

    /**
     * Base URL of the service.
     */
    String baseUrl

    /**
     * Constructor.
     *
     * @param httpClientFactory
     * @param baseUrl
     */
    ExampleAuthenticationFilter(HttpClientFactory httpClientFactory, String baseUrl) {
        this.httpClientFactory = httpClientFactory
        this.baseUrl = baseUrl
    }

    /**
     * Perform the logic of authenticating.
     *
     * This method, upon successful authentication, should update the {@link #authenticationToken}
     * property, and optionally the {@link #timeout} property if applicable.
     */
    @Override
    protected void authenticate() {
        def response = httpClientFactory.createHttpClient().addFilter(new PrintlnLoggingFilter()).get {
            uri = "${baseUrl}/authenticate"
            headers = ['Authorization': 'foobar']
        }
        setAuthenticationToken(response.getEntity(String))
    }

    /**
     * Retrieve the header name that should contain the authentication token.
     *
     * @return The header name that should contain the authentication token.
     */
    @Override
    protected String getAuthenticationTokenHeader() {
        return 'X-Auth-Token'
    }
}
