package com.app.oldYoung.domain.chatAI.service;

import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import com.app.oldYoung.domain.incomesnapshot.port.IncomeSnapshotPort;
import com.app.oldYoung.domain.incomesnapshot.repository.IncomeSnapshotRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class IncomSnapshotAdapter implements IncomeSnapshotPort {

  private final IncomeSnapshotRepository repo;

  IncomSnapshotAdapter(IncomeSnapshotRepository repo) {
    this.repo = repo;
  }

  // IncomeSnapshotPort 구현체
  @Override
  public Optional<IncomeSnapshot> findLatestByUserId(Long userId) {
    return repo.findTopByUserIdOrderByCreatedAtDesc(userId);
  }

}
