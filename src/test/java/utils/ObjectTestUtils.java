package utils;

import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.Genre;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.ContentInfoId;
import com.umc.sp.contents.persistence.model.id.GenresId;
import com.umc.sp.contents.persistence.model.id.TagId;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.util.List;
import java.util.UUID;
import static com.umc.sp.contents.persistence.model.type.CategoryType.TOPIC;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.RESOURCE_URL;

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
                      .genre(genre)
                      .specialityId(UUID.randomUUID());
    }

    public static ContentGroup.ContentGroupBuilder buildContentGroup(final Content parentContent, final Content content) {
        return ContentGroup.builder().id(new ContentGroupId(parentContent.getId().getId(), content.getId().getId())).sortOrder(0);
    }

    public static ContentInfo.ContentInfoBuilder buildContentInfo() {
        return ContentInfo.builder().id(new ContentInfoId()).type(RESOURCE_URL).value(UUID.randomUUID().toString());
    }

    public static Tag.TagBuilder buildTag(){
        return Tag.builder().id(new TagId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).global(false);
    }
}
