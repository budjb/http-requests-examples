package com.budjb.httprequests.examples

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.filter.bundled.AuthenticationTokenHeaderFilter
import com.budjb.httprequests.filter.bundled.LoggingFilter
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

/**
 * This would be better with spring security, but the example doesn't need that complexity.
 */
@RestController
@Slf4j
class Controller {
    /**
     * HTTP client factory instance.
     */
    @Autowired
    HttpClientFactory httpClientFactory

    /**
     * If an authorization header is given, just trust it.
     *
     * @param auth
     * @return
     */
    @RequestMapping('/authenticate')
    ResponseEntity<String> authenticate(@RequestHeader('Authorization') String auth) {
        if (auth) {
            return new ResponseEntity<String>('secret', HttpStatus.OK)
        }
        return new ResponseEntity<String>('Unauthorized', HttpStatus.UNAUTHORIZED)
    }

    /**
     * This is the privileged endpoint that checks for valid authentication.
     *
     * @param authToken Authentication token of the user.
     * @return
     */
    @RequestMapping('/privileged')
    ResponseEntity<String> privileged(@RequestHeader(value = 'X-Auth-Token', required = false) String authToken) {
        if (!authToken || authToken != 'secret') {
            return new ResponseEntity<String>("You are unauthorized to view this page.", HttpStatus.UNAUTHORIZED)
        }
        return new ResponseEntity<String>("Welcome to the secret page.", HttpStatus.OK)
    }

    /**
     * Hit this endpoint to test the functionality of the of the HTTP requests client.
     *
     * @param request
     * @return
     */
    @RequestMapping('/')
    String test(HttpServletRequest request) {
        def client = httpClientFactory.createHttpClient()
            .addFilter(new ExampleAuthenticationFilter(httpClientFactory, getBaseUrl(request)))
            .addFilter(new PrintlnLoggingFilter())

        def response = client.get {
            uri = "${getBaseUrl(request)}/privileged"
        }

        return response.getEntity(String)
    }

    /**
     * Return the base URL of the running servlet.
     *
     * @param request
     * @return
     */
    String getBaseUrl(HttpServletRequest request) {
        return "${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}/${request.getContextPath()}"
    }
}
