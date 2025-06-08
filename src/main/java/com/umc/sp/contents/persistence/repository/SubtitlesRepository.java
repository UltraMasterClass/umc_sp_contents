package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Subtitle;
import com.umc.sp.contents.persistence.model.id.SubtitleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtitlesRepository extends JpaRepository<Subtitle, SubtitleId> {
}
