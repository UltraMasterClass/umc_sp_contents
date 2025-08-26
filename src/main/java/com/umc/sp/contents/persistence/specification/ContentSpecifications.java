package com.umc.sp.contents.persistence.specification;

import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.TagTranslation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import static com.umc.sp.contents.persistence.model.type.ContentType.EXPERT;

public class ContentSpecifications {

    public static Specification<Content> isFeatured() {
        return (root, query, cb) -> cb.equal(root.get("featured"), true);
    }

    public static Specification<Content> hasExpertType() {
        return (root, query, cb) -> cb.equal(root.get("type"), EXPERT);
    }


    public static Specification<Content> hasTags(Set<UUID> tagIds) {
        return (root, query, cb) -> {
            Root<ContentTag> contentTagsRoot = query.from(ContentTag.class);
            var joinPredicate = cb.equal(contentTagsRoot.get("id").get("contentId"), root.get("id").get("id"));
            var tagIdPredicate = contentTagsRoot.get("id").get("tagId").in(tagIds);

            query.distinct(true);
            return cb.and(joinPredicate, tagIdPredicate);
        };
    }

    public static Specification<Content> hasCategories(Set<UUID> categoryIds) {
        return (root, query, cb) -> {
            Join<Content, Category> categoriesJoin = root.join("categories", JoinType.INNER);
            return categoriesJoin.get("id").get("id").in(categoryIds);
        };
    }

    public static Specification<Content> hasTagsNotIn(Set<UUID> tagIds) {
        return (root, query, cb) -> {
            // Ensure distinct for paging
            query.distinct(true);

            // Subquery to find matching tags to exclude
            Subquery<ContentTag> subquery = query.subquery(ContentTag.class);
            Root<ContentTag> ct = subquery.from(ContentTag.class);
            subquery.select(ct);
            subquery.where(
                    cb.equal(ct.get("id").get("contentId"), root.get("id").get("id")),
                    ct.get("id").get("tagId").in(tagIds)
            );

            return cb.not(cb.exists(subquery));
        };
    }

    public static Specification<Content> hasCategoriesNotIn(Set<UUID> categoryIds) {
        return (root, query, cb) -> {
            query.distinct(true);

            // Subquery for excluded categories
            Subquery<Content> subquery = query.subquery(Content.class);
            Root<Content> subRoot = subquery.from(Content.class);
            Join<Content, Category> join = subRoot.join("categories");

            subquery.select(subRoot)
                    .where(
                            cb.equal(subRoot.get("id"), root.get("id")),
                            join.get("id").get("id").in(categoryIds)
                    );

            return cb.not(cb.exists(subquery));
        };
    }

    public static Specification<Content> searchOnTitleOrCategoryOrTagContains(String search, String languageCode) {
        return (root, query, cb) -> {
            var pattern = "%" + search.toLowerCase() + "%";

            // Predicate for content.name
            //var nameLike = cb.like(cb.lower(root.get("name")), pattern);

            // Predicate for categories.code - DESACTIVADO
           /* Join<Content, Category> categoriesJoin = root.join("categories", JoinType.INNER);
            var categoryLike = cb.like(cb.lower(categoriesJoin.get("code")), pattern);*/

            // Predicates for tag_translation.value
            Root<ContentTag> contentTagsRoot = query.from(ContentTag.class);
            Root<Tag> tagsRoot = query.from(Tag.class);
            Root<TagTranslation> tagTranslationRoot = query.from(TagTranslation.class);

            // joins:
            var joinContent = cb.equal(contentTagsRoot.get("id").get("contentId"), root.get("id").get("id"));
            //TODO: join global tags code on the like search
            var joinTags = cb.equal(contentTagsRoot.get("id").get("tagId"), tagsRoot.get("id").get("id"));
            var joinTranslation = cb.equal(tagTranslationRoot.get("tag").get("id"), tagsRoot.get("id"));
            var languageMatch = cb.equal(tagTranslationRoot.get("id").get("languageCode"), languageCode);
            var valueLike = cb.like(cb.lower(tagTranslationRoot.get("value")), pattern);
            var tagTranslationPredicate = cb.and(joinContent, joinTags, joinTranslation, languageMatch, valueLike);

            query.distinct(true);
            // Búsqueda por nombre del contenido O por valor de traducción del tag
            // NOTA: La búsqueda por category.code está desactivada
            //return cb.or(nameLike, tagTranslationPredicate);
            return cb.or(tagTranslationPredicate);
        };
    }
}
