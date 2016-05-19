package com.budjb.httprequests.examples

import com.budjb.httprequests.HttpClientFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
public class Application {
    /**
     * HTTP Client Factory.
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
        new SpringApplicationBuilder().listeners()
        SpringApplication.run(Application, args)
    }

    /**
     * Scans the classpath for an {@link HttpClientFactory} implementation and returns an instance of it.
     *
     * This method, since it is annotated with {@link Bean}, will register a Spring bean named "httpClientFactory"
     * that can be injected into any other managed Spring bean.
     *
     * Using this classpath scanning logic helps to prevent an application from needing to know what underlying
     * HTTP client is being used.
     *
     * @return
     */
    @Bean
    HttpClientFactory httpClientFactory() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(new AssignableTypeFilter(HttpClientFactory))

        List<Class<? extends HttpClientFactory>> candidates = provider.findCandidateComponents('com.budjb.httprequests').collect {
            Class.forName(it.getBeanClassName()) as Class<? extends HttpClientFactory>
        }

        if (candidates.size() == 0) {
            throw new IllegalStateException("no provider with type 'com.budjb.httprequests.HttpClientFactory' found on the classpath")
        }
        else if (candidates.size() > 1) {
            throw new IllegalStateException("multiple providers with type 'com.budjb.httprequests.HttpClientFactory' found on the classpath: ${candidates*.simpleName.join(', ')}")
        }

        Class<? extends HttpClientFactory> clazz = candidates.get(0)

        return clazz.newInstance()
    }

    /**
     * An endpoint at the root of the application that uses the registered {@link HttpClientFactory}
     * to retrieve the webpage at https://icanhazip.com, which simply prints your IP address,
     * and returns it to the web client.
     *
     * @return
     */
    @RequestMapping('/')
    String index() {
        return httpClientFactory.createHttpClient().get {
            uri = 'https://icanhazip.com'
            sslValidated = false
        }.getEntity(String)
    }
}
