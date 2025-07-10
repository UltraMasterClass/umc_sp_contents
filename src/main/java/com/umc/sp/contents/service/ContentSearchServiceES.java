package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.response.ContentsDto;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "content.search.strategy.db.enabled", havingValue = "false")
public class ContentSearchServiceES implements ContentSearchService{

    @Override
    public ContentsDto searchContent(final Set<String> tagCodes, final Set<String> categoryNames, final String search, final int offset, final int limit) {
        //TODO: implement when elastic search is integrated
        return new ContentsDto();
    }
}
