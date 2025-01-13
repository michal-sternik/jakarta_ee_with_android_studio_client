package com.example.stm45;

import jakarta.xml.ws.Endpoint;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class SoapPublisher implements ServletContextListener {
    public static final String URL = "http://0.0.0.0:8090/MapService";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing SoapPublisher...");
        try {
            Endpoint.publish(URL, new MapService());
            System.out.println("SOAP Service published at: " + URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
}
