package com.umc.sp.contents.controller;

import com.umc.sp.contents.IntegrationTest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

public class ContentControllerIntegrationTest implements IntegrationTest{

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Clock clock;

    @Test
    void shouldFindUmcContentById() throws JSONException {
        //given
        var expected = new HashMap<>();
        expected.put("timeStamp", LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME));
        expected.put("status", "OK");
        expected.put("user_id", "c4d87e59-7507-44d8-963e-ec39a64d246d");
        expected.put("message", "Content  operations  successfully");

        //when
        var result = restTemplate.getForEntity("/api/content", HashMap.class);

        //then
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).containsExactlyInAnyOrderEntriesOf(expected);
    }

}
