package com.umc.sp.contents.controller;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentsController {

    private final Clock clock;

    @RequestMapping(value = "/video", method = RequestMethod.GET,produces = "application/json")
    public ResponseEntity<Map<String, Object>> findUmcVideo() {
        log.info("Starting Content Administration....");

        Map<String, Object> response = new HashMap<>();
        response.put("timeStamp", LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME));
        response.put("status", "OK");
        response.put("content_id", "b8ef44b4-b49b-48d8-8d21-493cb1adb9ba");
        response.put("message", "Video  operations  successfully");

        log.info("Finishing Content Administration....");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
