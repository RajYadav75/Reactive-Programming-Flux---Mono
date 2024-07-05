package in.raj.controller;

import in.raj.binding.CustomerEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

@RestController
public class CustomerRestController {
    @GetMapping(value = "/event" , produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<CustomerEvent> getEvent(){
        CustomerEvent event = new CustomerEvent("Raj",new Date());
        return Mono.justOrEmpty(event);
    }
    @GetMapping(value = "/events",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CustomerEvent> getEvents(){
        //creating customer data in the form of object
        CustomerEvent event = new CustomerEvent("Raja",new Date());
        //Create Stream object to send the data
        Stream<CustomerEvent> customerEventStream  = Stream.generate(()->event);
        //create Flux object with stream
        Flux<CustomerEvent> customerEventFlux = Flux.fromStream(customerEventStream);
        //Setting Response Interval
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(3));
        //Combine Flux Interval and Customer Flux
        Flux<Tuple2<Long,CustomerEvent>> zip = Flux.zip(interval, customerEventFlux);
        //Getting Flux value from the zip
        Flux<CustomerEvent> fluxMap = zip.map(Tuple2::getT2);
        //Returning Flux Response
        return new ResponseEntity<>(fluxMap, HttpStatus.OK).getBody();
    }
}
