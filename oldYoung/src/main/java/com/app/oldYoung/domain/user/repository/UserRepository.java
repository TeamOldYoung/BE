package com.app.oldYoung.domain.user.repository;

import com.app.oldYoung.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.provider = :provider AND u.providerId = :providerId ORDER BY u.createdAt ASC")
    List<User> findAllByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    default Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        List<User> users = findAllByProviderAndProviderId(provider, providerId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }
}
