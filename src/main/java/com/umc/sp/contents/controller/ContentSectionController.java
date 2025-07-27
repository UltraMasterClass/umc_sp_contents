package com.umc.sp.contents.controller;

import com.umc.sp.contents.dto.response.ContentSectionsDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.dto.response.ExplorerDto;
import com.umc.sp.contents.exception.BadRequestException;
import com.umc.sp.contents.manager.ContentSectionManager;
import com.umc.sp.contents.persistence.model.type.ContentSectionViewType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class ContentSectionController {

    private final ContentSectionManager contentSectionManager;

    @GetMapping(value = "/{viewType}/main", produces = "application/json")
    public Mono<ResponseEntity<ContentSectionsDto>> searchContent(@PathVariable String viewType,
                                                                  //TODO: support as header or param? :O
                                                                  @RequestParam(required = false, defaultValue = "es") String langCode,
                                                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                                                  @RequestParam(required = false, defaultValue = "20") int limit) {
        var sectionViewType = ContentSectionViewType.from(viewType)
                                                    .orElseThrow(() -> new BadRequestException(String.format("viewType %s not supported", viewType)));
        return contentSectionManager.getContentSections(sectionViewType, offset, getLimit(limit), langCode).map(dto -> ResponseEntity.ok().body(dto));
    }

    @PostMapping(value = "/explore", produces = "application/json")
    public Mono<ResponseEntity<ContentsDto>> searchContent(@RequestBody @Valid ExplorerDto explorerDto) {
        //TODO: support language on header or explorer? :O
        return contentSectionManager.searchByExplorer(explorerDto, "es").map(dto -> ResponseEntity.ok().body(dto));
    }

    private int getLimit(final int limit) {
        return (limit > 0) ? limit : 20;
    }
}
