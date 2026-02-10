package luckydrop.demo.user.repository;

import jakarta.validation.constraints.Size;
import luckydrop.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByNickname(@Size(min = 2, message = "닉네임은 최소 2자 이상이어야 합니다.") String nickname);
}
