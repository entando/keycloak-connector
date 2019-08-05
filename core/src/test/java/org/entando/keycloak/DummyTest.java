package org.entando.keycloak;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DummyTest {
    @Test
    public void testAccesToken(){
        //Given
        AccessToken at = new AccessToken();
        at.setExpiresIn(100);
        at.setCreatedTimestamp(Instant.now().minus(101,ChronoUnit.SECONDS).toEpochMilli());
        //Expect
        assertThat(at.isExpired(), is(Boolean.TRUE));
    }

}
