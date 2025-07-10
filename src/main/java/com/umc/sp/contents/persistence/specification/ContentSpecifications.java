package com.umc.sp.contents.persistence.specification;

import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.Tag;
import jakarta.persistence.criteria.Root;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class ContentSpecifications {


    public static Specification<Content> hasTags(Set<String> tagCodes) {
        return (root, query, cb) -> {
            final Root<ContentTag> contentTagsRoot = query.from(ContentTag.class);
            final Root<Tag> tagsRoot = query.from(Tag.class);

            var joinContent = cb.equal(contentTagsRoot.get("id").get("contentId"), root.get("id").get("id"));
            var joinTags = cb.equal(contentTagsRoot.get("id").get("tagId"), tagsRoot.get("id").get("id"));
            var filterPredicate = tagsRoot.get("code").in(tagCodes);

            query.distinct(true);
            return cb.and(joinContent, joinTags, filterPredicate);
        };
    }

    public static Specification<Content> hasCategories(Set<String> categoryNames) {
        return (root, query, cb) -> root.get("category").get("code").in(categoryNames);
    }

    public static Specification<Content> searchOnTitleOrCategoryOrTagContains(String search) {
        return (root, query, cb) -> {
            var pattern = "%" + search.toLowerCase() + "%";
            var nameLike = cb.like(cb.lower(root.get("name")), pattern);
            var categoryLike = cb.like(cb.lower(root.get("category").get("code")), pattern);

            // Join tags via content_tags
            final Root<ContentTag> contentTagsRoot = query.from(ContentTag.class);
            final Root<Tag> tagsRoot = query.from(Tag.class);

            // predicates
            var joinContent = cb.equal(contentTagsRoot.get("id").get("contentId"), root.get("id").get("id"));
            var joinTags = cb.equal(contentTagsRoot.get("id").get("tagId"), tagsRoot.get("id").get("id"));
            var tagLike = cb.and(joinContent, joinTags, cb.like(cb.lower(tagsRoot.get("code")), pattern));

            query.distinct(true);

            return cb.or(nameLike, categoryLike, tagLike);
        };
    }
}
