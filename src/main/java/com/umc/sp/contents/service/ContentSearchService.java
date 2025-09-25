package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.response.ContentsDto;
import java.util.Set;
import java.util.UUID;

public interface ContentSearchService {
    ContentsDto searchContent(final Set<UUID> tagCodes, final Set<UUID> categoryIds, final String search, final String langCode, final int offset, final int limit);
    
    ContentsDto searchContent(final Set<UUID> tagCodes, 
                             final Set<UUID> categoryIds, 
                             final Set<UUID> excludeTags,
                             final Set<UUID> excludeCategories,
                             final String search, 
                             final String langCode, 
                             final int offset, 
                             final int limit);
}
