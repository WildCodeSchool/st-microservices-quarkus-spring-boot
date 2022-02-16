package dev.wcs.tutoring.microservice.weatherbot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.camel.quarkus.main.CamelMainApplication;

@QuarkusMain
public class CamelApplicationStarter {

    public static void main(String ... args) {
        // WOW?
        Quarkus.run(CamelMainApplication.class, args);
    }
}