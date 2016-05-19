package com.budjb.httprequests.examples

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.filter.bundled.ConsoleLoggingFilter
import com.budjb.httprequests.jersey2.JerseyHttpClientFactory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

@Slf4j
@RestController
@SpringBootApplication
public class Application {
    /**
     * HTTP client factory instance.
     */
    @Autowired
    HttpClientFactory httpClientFactory

    /**
     * Application entry point.
     *
     * @param args
     * @throws Exception
     */
    static void main(String[] args) throws Exception {
        SpringApplication.run(Application, args)
    }

    /**
     * Creates the httpClientFactory bean.
     *
     * @return
     */
    @Bean
    HttpClientFactory httpClientFactory() {
        return new JerseyHttpClientFactory()
    }

    /**
     * Authentication endpoint. If an authorization header is given, just trust it.
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
            .addFilter(new ConsoleLoggingFilter())

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
