package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.response.ContentsDto;
import java.util.Set;

public interface ContentSearchService {
    ContentsDto searchContent(final Set<String> tagCodes, final Set<String> categoryNames, final String search, final int offset, final int limit);
}
