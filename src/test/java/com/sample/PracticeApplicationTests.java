package com.sample;

import com.sample.model.Service;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(properties = {"kafka-requests-path=direct:requests"})
//@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
public class PracticeApplicationTests {

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:jpa:com.sample.entity.Service")
    public MockEndpoint saveToDb;

    @EndpointInject("mock:kafka:results")
    public MockEndpoint kafkaResults;

    @EndpointInject("mock:kafka:status_topic")
    public MockEndpoint kafkaStatusTopic;

    @Test
    public void canSaveToDataBase() throws InterruptedException {
        Service service = new Service();
        service.setSessionID(1);
        service.setMessage("hi");
        saveToDb.expectedBodiesReceived(service);

        producerTemplate.sendBody("direct:requests", "<service><sessionID>1</sessionID>" +
                "<message>hi</message></service>");

        MockEndpoint.assertIsSatisfied(saveToDb);
    }

    @Test
    public void canGetCorrectResults() throws InterruptedException {
        kafkaResults.expectedBodiesReceived("{\"sessionID\":2,\"message\":\"hi\"}");

        producerTemplate.sendBody("direct:requests", "<service><sessionID>2</sessionID>" +
                "<message>hi</message></service>");

        MockEndpoint.assertIsSatisfied(kafkaResults);
    }

    @Test
    public void canSendOKStatus() throws InterruptedException {
        kafkaStatusTopic.expectedBodiesReceived("<status>ok</status>");

        producerTemplate.sendBody("direct:requests", "<service><sessionID>98</sessionID>" +
                "<message>120</message></service>");

        kafkaStatusTopic.assertIsSatisfied(5000);
    }

    @Test
    public void canSendErrorStatus() throws InterruptedException {
        kafkaStatusTopic.expectedBodiesReceived("<status>error</status><message>Unmarshaling failed</message>");

        producerTemplate.sendBody("direct:requests", "<not_service><sessionID>98</sessionID>" +
                "<message>120</message></service>");

        kafkaStatusTopic.assertIsSatisfied(5000);
    }
}
