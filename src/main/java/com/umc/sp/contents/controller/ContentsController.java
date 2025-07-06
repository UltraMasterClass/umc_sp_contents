package com.umc.sp.contents.controller;

import com.umc.sp.contents.controller.dto.response.ContentDetailDto;
import com.umc.sp.contents.controller.dto.response.ContentsDto;
import com.umc.sp.contents.manager.ContentServiceManager;
import com.umc.sp.contents.persistence.model.id.ContentId;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentsController {

    private final Clock clock;
    private final ContentServiceManager contentServiceManager;

    @RequestMapping(value = "/content", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> findUmcVideo() {
        log.info("Starting Content Administration....");

        Map<String, Object> response = new HashMap<>();
        response.put("timeStamp", LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME));
        response.put("status", "OK");
        response.put("content_id", "b8ef44b4-b49b-48d8-8d21-493cb1adb9ba");
        response.put("message", "Content  operations  successfully");

        log.info("Finishing Content Administration....");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/content/{contentId}", method = RequestMethod.GET, produces = "application/json")
    public Mono<ResponseEntity<ContentDetailDto>> getContentById(@PathVariable String contentId) {
        return contentServiceManager.getContentById(new ContentId(contentId)).map(contentDetailDto -> ResponseEntity.ok().body(contentDetailDto));
    }

    @RequestMapping(value = "/content/{contentId}/content", method = RequestMethod.GET, produces = "application/json")
    public Mono<ResponseEntity<ContentsDto>> getContentByParentId(@PathVariable String contentId,
                                                                  @RequestParam(defaultValue = "0") int offset,
                                                                  @RequestParam(defaultValue = "20") int limit) {
        return contentServiceManager.getContentByParentId(new ContentId(contentId), offset, Math.max(limit, 20))
                                    .map(contentDetailDto -> ResponseEntity.ok().body(contentDetailDto))
                                    // TODO: handle on controller advice
                                    .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                                    .onErrorResume(Exception.class, e -> {
                                        log.error("Error retrieving content of parent content {}: ", contentId, e);
                                        return Mono.just(ResponseEntity.internalServerError().build());
                                    });
    }
}
