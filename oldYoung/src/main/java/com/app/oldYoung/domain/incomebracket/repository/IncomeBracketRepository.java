package com.app.oldYoung.domain.incomebracket.repository;

import com.app.oldYoung.domain.incomebracket.entity.IncomeBracket;
import com.app.oldYoung.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeBracketRepository extends JpaRepository<IncomeBracket, Long> {
    Optional<IncomeBracket> findByUser(User user);
}