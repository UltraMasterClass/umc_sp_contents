package utils;

import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.ContentSection;
import com.umc.sp.contents.persistence.model.ContentSectionCriteria;
import com.umc.sp.contents.persistence.model.Genre;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.ContentInfoId;
import com.umc.sp.contents.persistence.model.id.ContentSectionCriteriaId;
import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import com.umc.sp.contents.persistence.model.id.GenresId;
import com.umc.sp.contents.persistence.model.id.TagId;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaType;
import com.umc.sp.contents.persistence.model.type.ContentSectionSortType;
import com.umc.sp.contents.persistence.model.type.ContentSectionType;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.util.List;
import java.util.UUID;
import static com.umc.sp.contents.persistence.model.type.CategoryType.TOPIC;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.RESOURCE_URL;
import static com.umc.sp.contents.persistence.model.type.ContentSectionViewType.DISCOVERY;

public class ObjectTestUtils {

    public static Category.CategoryBuilder buildCategory() {
        return Category.builder().id(new CategoryId()).type(TOPIC).description(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }

    public static Genre.GenreBuilder buildGenre() {
        return Genre.builder().id(new GenresId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }

    public static Content.ContentBuilder buildContent(final Category category, final Genre genre) {
        return Content.builder()
                      .id(new ContentId())
                      .featured(true)
                      .type(ContentType.VIDEO)
                      .structureType(ContentStructureType.EPISODE)
                      .categories(List.of(category))
                      .name(UUID.randomUUID().toString())
                      .description(UUID.randomUUID().toString())
                      .genre(genre);
    }

    public static ContentGroup.ContentGroupBuilder buildContentGroup(final Content parentContent, final Content content) {
        return ContentGroup.builder().id(new ContentGroupId(parentContent.getId().getId(), content.getId().getId())).sortOrder(0);
    }

    public static ContentInfo.ContentInfoBuilder buildContentInfo() {
        return ContentInfo.builder().id(new ContentInfoId()).type(RESOURCE_URL).value(UUID.randomUUID().toString());
    }

    public static Tag.TagBuilder buildTag() {
        return Tag.builder().id(new TagId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).global(false);
    }

    public static ContentSection.ContentSectionBuilder buildContentSection() {
        return ContentSection.builder()
                             .id(new ContentSectionId())
                             .viewType(DISCOVERY)
                             .contentType(ContentSectionType.REGULAR)
                             .title(UUID.randomUUID().toString())
                             .titleCode(UUID.randomUUID().toString())
                             .sortOrder(0)
                             .numberOfElements(4)
                             .sortType(ContentSectionSortType.PRIORITY);
    }

    public static ContentSectionCriteria.ContentSectionCriteriaBuilder<?, ?> buildContentSectionCriteria(final ContentSectionId contentSectionId) {
        return ContentSectionCriteria.builder()
                                     .id(new ContentSectionCriteriaId())
                                     .contentSectionId(contentSectionId)
                                     .type(ContentSectionCriteriaType.TAG)
                                     .relationType(ContentSectionCriteriaRelationType.AND)
                                     .referenceIds(UUID.randomUUID() + "," + UUID.randomUUID());
    }

}
