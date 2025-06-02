package com.umc.sp.contents.actuator;

import com.umc.sp.contents.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;


class ActuatorIntegrationTest implements IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnHealthStatus() throws JSONException {
        //given
        var expected = "{\"status\":\"UP\"}";

        //when
        var result = restTemplate.getForEntity("/actuator/health",String.class);

        //then
        Assertions.assertThat(HttpStatus.OK).isEqualTo(result.getStatusCode());
        JSONAssert.assertEquals(expected, result.getBody(), true);
    }
}
