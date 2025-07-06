package com.umc.sp.contents.service;

import com.umc.sp.contents.controller.dto.response.ContentDetailDto;
import com.umc.sp.contents.controller.dto.response.ContentsDto;
import com.umc.sp.contents.converter.ContentConverter;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.repository.TagsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final TagsRepository tagsRepository;
    private final ContentConverter contentConverter;

    @Transactional(readOnly = true)
    public ContentDetailDto getContentById(final ContentId id) {
        // TODO: add custom exceptions and Advice handler
        var content = contentRepository.findById(id).orElseThrow();
        var contentTags = tagsRepository.findContentTagsByContentId(content.getId().getId());
        var parentId = contentGroupRepository.findByIdContentId(id.getId()).map(contentGroup -> contentGroup.getId().getParentContentId()).orElse(null);
        return contentConverter.convertToDetailDto(content, parentId, contentTags);
    }

    @Transactional(readOnly = true)
    public ContentsDto getContentByParentId(final ContentId parentId, final int offset, final int limit) {
        var pageable = PageRequest.of(offset / limit, limit);
        var contents = contentRepository.findByParentIdAndDisableDateIsNull(parentId.getId(), pageable);
        var dtos = contents.stream().map(content -> contentConverter.convertToDto(content, parentId.getId())).toList();
        return ContentsDto.builder().contents(dtos).hasNext(contents.hasNext()).build();
    }
}
