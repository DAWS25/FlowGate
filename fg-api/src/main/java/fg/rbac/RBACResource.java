package fg.rbac;

import java.util.HashMap;
import java.util.Map;

import fg.rbac.github.GHTeamsRBACProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/rbac")
public class RBACResource {

    @Inject
    GHTeamsRBACProvider ghTeams;

    @GET
    @Produces(APPLICATION_JSON)
    public RBACResponse getRolesByUsername(
            @QueryParam("github_username") String github_username,
            @QueryParam("evict") @DefaultValue("false") boolean evict) {
        Map<String, Role> roles = new HashMap<>();
        if (github_username != null && !github_username.isBlank()) {
            for (Role role : ghTeams.getRolesForUsername(github_username, evict)) {
                if (roles.containsKey(role.getRoleName())) {
                    System.out.println("Duplicate role name detected: " + role.getRoleName() + ". Overwriting existing role.");
                }
                roles.put(role.getRoleName(), role);
            }
        }
        return new RBACResponse(roles);
    }
}
