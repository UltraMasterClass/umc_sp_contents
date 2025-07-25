package com.umc.sp.contents.service;

import com.umc.sp.contents.mapper.TagMapper;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.TagTranslation;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.id.TagTranslationId;
import com.umc.sp.contents.persistence.repository.ContentTagRepository;
import com.umc.sp.contents.persistence.repository.TagTranslationsRepository;
import com.umc.sp.contents.persistence.repository.TagsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagsRepository tagsRepository;
    private final TagTranslationsRepository tagTranslationsRepository;
    private final ContentTagRepository contentTagRepository;
    private final TagMapper tagMapper;

    @Transactional
    public void createContentDefaultTags(@NonNull final Content content){
        final Tag tag = tagsRepository.save(tagMapper.fromContent(content));
        var tagTranslation = TagTranslation.builder().id(new TagTranslationId(tag.getId(), "es")).value(tag.getCode()).tag(tag).build();
        tagTranslationsRepository.save(tagTranslation);
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
    }



}
