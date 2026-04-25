package fg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CIFailureDemoTest {

    @Test
    void deliberatelyFails() {
        assertEquals(1, 2, "intentional failure to verify CI flags failing tests");
    }
}
