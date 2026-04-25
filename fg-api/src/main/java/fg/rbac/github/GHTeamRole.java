package fg.rbac.github;

import fg.rbac.Role;

public record GHTeamRole(String teamName) implements Role {
    @Override
    public String getRoleName() {
        return teamName;
    }

    @Override
    public String getProvider() {
        return "github";
    }
}
