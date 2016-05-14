package com.budjb.httprequests.examples

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.jersey2.JerseyHttpClientFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
public class Application {
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
     * HTTP client factory used to make HTTP requests.
     */
    @Bean
    HttpClientFactory httpClientFactory() {
        return new JerseyHttpClientFactory()
    }
}
