package com.sample.route;

import com.sample.dto.ServiceDTO;
import com.sample.generated.Service;
import com.sample.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveRouter extends RouteBuilder {
    private final ServiceMapper mapper;
    private long startTime = 0;
    private String messageBody;

    public void configure() {
        from("direct:save_to_db")
            .choice()
            .when(body().isInstanceOf(Service.class))
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .process(exchange -> {
                    Service in = exchange.getIn().getBody(Service.class);
                    com.sample.model.Service service = mapper.mapGenerated(in);

                    exchange.getMessage().setBody(service, com.sample.model.Service.class);
                })
                .log("Saving ${body} to database...")
                .to("jpa:com.sample.entity.Service")
                .process(exchange -> {
                    com.sample.model.Service in = exchange.getIn().getBody(com.sample.model.Service.class);
                    ServiceDTO service= mapper.mapWithoutId(in);

                    exchange.getMessage().setBody(service, ServiceDTO.class);
                })
                .marshal().json(JsonLibrary.Jackson)
                .log("Saving ${body} to kafka")
                .to("kafka:results?brokers=localhost:9092")
                .setBody(simple("<status>ok</status>"))
                .to("direct:status")
                .to("direct:metrics_router_increment_succeeded_messages")
                .to("direct:metrics_router_stop_timer")
            .otherwise()
                .setBody(simple("<status>error</status><message>XML data isn't instance of Service</message>"))
                .to("direct:status")
                .to("direct:metrics_router_increment_failed_messages")
                .to("direct:metrics_router_stop_timer");

        from("direct:metrics_router_increment_total_messages")
                .to("sql:UPDATE metrics SET total = total + 1;");
        from("direct:metrics_router_increment_failed_messages")
                .to("sql:UPDATE metrics SET failed = failed + 1;");
        from("direct:metrics_router_increment_succeeded_messages")
                .to("sql:UPDATE metrics SET succeeded = succeeded + 1;");
        from("direct:metrics_router_start_timer")
                .process(exchange -> {
                    startTime = System.currentTimeMillis();
                    messageBody = exchange.getIn().getBody(String.class);
                });
        from("direct:metrics_router_stop_timer")
                .process(exchange -> {
                    exchange.setProperty("message", messageBody);
                    exchange.setProperty("time", System.currentTimeMillis() - startTime);
                })
                .to("sql:INSERT INTO message_info(message, time) VALUES(:#${exchangeProperty.message}, :#${exchangeProperty.time});");
    }
}

