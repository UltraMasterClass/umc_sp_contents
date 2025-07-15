package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.response.ContentsDto;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "content.search.strategy.db.enabled", havingValue = "false")
public class ContentSearchServiceES implements ContentSearchService{

    @Override
    public ContentsDto searchContent(final Set<UUID> tagCodes, final Set<UUID> categoryIds, final String search, final String langCode, final int offset, final int limit) {
        //TODO: implement when elastic search is integrated
        throw new RuntimeException("Elastic Search queries not supported");
    }
}
