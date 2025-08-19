package com.app.oldYoung.domain.incomesnapshot.port;

import com.app.oldYoung.domain.incomesnapshot.entity.IncomeSnapshot;
import java.util.Optional;

public interface IncomeSnapshotPort {
  Optional<IncomeSnapshot> findLatestByUserId(Long userId);
}
