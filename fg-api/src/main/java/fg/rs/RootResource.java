package fg.rs;

import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.*;

@Path("/")
public class RootResource {
    @GET
    @Produces(TEXT_PLAIN)
    public String get() {
        return "Welcome to FlowGate API";
    }
}   