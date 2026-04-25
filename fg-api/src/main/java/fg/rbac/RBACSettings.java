package fg.rbac;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "rbac")
@StaticInitSafe
public interface RBACSettings {
    @WithDefault("333") // Half a beast
    Integer defaultRoleExpirySeconds();

    @WithDefault("DAWS25") // Default GitHub organization
    String githubOrg();

    /**
     * GitHub personal access token for API authentication.
     * Should have the following scopes: read:org, read:user
     */
    String githubToken();
}
