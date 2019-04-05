package lt.gzeskas.payment.web.controller.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
public class StatusController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Status getStatus() {
        return new Status("OK");
    }

}
