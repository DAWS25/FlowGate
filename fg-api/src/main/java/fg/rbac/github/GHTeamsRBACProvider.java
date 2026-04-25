package fg.rbac.github;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;

import fg.rbac.RBACSettings;
import fg.rbac.Role;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class GHTeamsRBACProvider {

    private static final Logger log = Logger.getLogger(GHTeamsRBACProvider.class.getName());

    @Inject
    RBACSettings settings;

    protected Cache<String, List<Role>> rolesCache;

    @PostConstruct
    void initCache() {
        rolesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(settings.defaultRoleExpirySeconds(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * Retrieves all GitHub team roles for a given username within the configured organization.
     *
     * This method queries the GitHub API to find all teams that the specified user belongs to
     * within the organization configured in RBACSettings (defaults to "DAWS25").
     *
     * @param ghUsername the GitHub username to query team memberships for
     * @return a list of GHTeamRole objects representing the teams the user belongs to,
     *         empty list if the user is not a member of any teams or if the user doesn't exist
     * @throws IllegalArgumentException if ghUsername is null or empty
     * @throws RuntimeException if there's an error communicating with the GitHub API
     */
    public List<Role> getRolesForUsername(String ghUsername, boolean evict) {
        if (ghUsername == null || ghUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("GitHub username cannot be null or empty");
        }

        if (evict) {
            rolesCache.invalidate(ghUsername);
        }

        try {
            return rolesCache.get(ghUsername, () -> fetchRolesForUsername(ghUsername));
        } catch (ExecutionException | UncheckedExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException("Failed to fetch GitHub team memberships for user: " + ghUsername, cause);
        }
    }

    protected List<Role> fetchRolesForUsername(String ghUsername) {
        try {
            GitHub github = new GitHubBuilder()
                    .withOAuthToken(settings.githubToken())
                    .build();

            log.info("Fetching team memberships for user: " + ghUsername + " in org: " + settings.githubOrg());

            GHOrganization org = github.getOrganization(settings.githubOrg());
            if (org == null) {
                log.warning("Organization not found: " + settings.githubOrg());
                return List.of();
            }

            GHUser user = github.getUser(ghUsername);
            if (user == null) {
                log.warning("User not found: " + ghUsername);
                return List.of();
            }

            return org.listTeams()
                    .toList()
                    .stream()
                    .filter(team -> team.hasMember(user))
                    .<Role>map(team -> new GHTeamRole(team.getName()))
                    .toList();

        } catch (IOException e) {
            log.severe("Error communicating with GitHub API: " + e.getMessage());
            throw new RuntimeException("Failed to fetch GitHub team memberships for user: " + ghUsername, e);
        }
    }
    
}
