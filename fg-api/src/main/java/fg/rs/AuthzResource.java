package fg.rs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;


@Path("/authz")
public class AuthzResource {
    private static final List<String> authorizedEmails = List.of(
            "jfaerman@gmail.com"
    );
    
    @GET
    @Produces(TEXT_PLAIN)
    public Response getAuthzByEmail(@QueryParam("email") String email) {
        if (email == null || email.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email query parameter is required")
                    .build();
        }
        
        if (!authorizedEmails.contains(email)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You are not authorized to access this resource")
                    .build();
        }
        
        var ok = Response.ok("You are authorized to access this resource").build();
        return ok;
    }
}   