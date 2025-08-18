package com.app.oldYoung.domain.incomesnapshot.repository;

import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeSnapshotRepository extends JpaRepository<IncomeSnapshot, Long> {

  // 최신 유저 소득분위 조회
  Optional<IncomeSnapshot> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
