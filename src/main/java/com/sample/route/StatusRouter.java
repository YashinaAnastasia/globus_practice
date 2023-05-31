package com.sample.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class StatusRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:status")
                .log("Send to status_topic : ${body}")
                .to("kafka:status_topic?brokers=localhost:9092");
    }
}
