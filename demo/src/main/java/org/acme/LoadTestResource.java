package org.acme;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/load")
public class LoadTestResource {

    @Inject
    MeterRegistry registry;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public String getCount() {
        Counter counter = registry.find("load.count_num").counter();
        double val = 0;
        if (counter != null) {
            val = counter.count();
        }
        return "Current count is " + (int) val;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public String Count() {
        registry.counter("load.count_num").increment();
        return "The counter is incremented";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/wait")
    @Timed("load.random_wait_time")
    public Uni<String> RandomWait() {
        long delay = ThreadLocalRandom.current().nextLong(1000, 5000);
        return Uni.createFrom().item("Wait for" + delay + "ms").onItem().delayIt().by(Duration.ofMillis(delay));
    }
}
