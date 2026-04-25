package fg.rbac.github;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fg.rbac.Role;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@QuarkusTest
class GHTeamsRBACProviderTest {

    @Inject
    TestGHTeamsRBACProvider provider;

    @BeforeEach
    void reset() {
        provider.rolesCache.invalidateAll();
        provider.fetchCount.set(0);
    }

    @Test
    void cachesAcrossCalls() {
        List<Role> first = provider.getRolesForUsername("alice", false);
        List<Role> second = provider.getRolesForUsername("alice", false);

        assertEquals(1, provider.fetchCount.get(), "second call should be served from cache");
        assertEquals(first, second);
    }

    @Test
    void evictForcesRefetch() {
        provider.getRolesForUsername("alice", false);
        provider.getRolesForUsername("alice", true);

        assertEquals(2, provider.fetchCount.get(), "evict=true should refetch");
    }

    @Test
    void differentUsersCachedSeparately() {
        provider.getRolesForUsername("alice", false);
        provider.getRolesForUsername("bob", false);
        provider.getRolesForUsername("alice", false);
        provider.getRolesForUsername("bob", false);

        assertEquals(2, provider.fetchCount.get(), "each distinct username triggers one fetch");
    }

    @Test
    void rejectsBlankUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> provider.getRolesForUsername(null, false));
        assertThrows(IllegalArgumentException.class,
                () -> provider.getRolesForUsername("", false));
        assertThrows(IllegalArgumentException.class,
                () -> provider.getRolesForUsername("   ", false));
        assertEquals(0, provider.fetchCount.get(), "validation failures must not hit GitHub");
    }

    @Test
    void returnsRolesFromFetcher() {
        List<Role> roles = provider.getRolesForUsername("alice", false);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(new GHTeamRole("admins")));
    }

    @Mock
    @Singleton
    static class TestGHTeamsRBACProvider extends GHTeamsRBACProvider {

        final AtomicInteger fetchCount = new AtomicInteger();

        @Override
        protected List<Role> fetchRolesForUsername(String ghUsername) {
            fetchCount.incrementAndGet();
            return switch (ghUsername) {
                case "alice" -> List.of(new GHTeamRole("admins"));
                case "bob" -> List.of(new GHTeamRole("readers"), new GHTeamRole("writers"));
                default -> List.of();
            };
        }
    }
}
