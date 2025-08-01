package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.custom.ContentCountByParentId;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_groups")
@NamedNativeQuery(name = "getChildContentCountByParentContentIds", query = """
           SELECT count(content_id) AS child_content_count, parent_content_id
           FROM content_groups
           WHERE  parent_content_id IN (:contentIds)
           AND disabled_date IS NULL
           GROUP BY parent_content_id
        """, resultSetMapping = "getChildContentCountByParentContentIdsMapping")
@SqlResultSetMapping(name = "getChildContentCountByParentContentIdsMapping", classes = @ConstructorResult(targetClass = ContentCountByParentId.class, columns = {
        @ColumnResult(name = "child_content_count", type = Integer.class), @ColumnResult(name = "parent_content_id", type = UUID.class)
}))
public class ContentGroup {

    @EmbeddedId
    private ContentGroupId id;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "disabled_date")
    private LocalDateTime disabledDate;
}
