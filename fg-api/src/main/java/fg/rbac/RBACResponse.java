package fg.rbac;

import java.util.Map;

public class RBACResponse {
    public Map<String, Role> roles;

    public RBACResponse() {
    }

    public RBACResponse(Map<String, Role> roles) {
        this.roles = roles;
    }

    public Map<String, Role> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Role> roles) {
        this.roles = roles;
    }
}
