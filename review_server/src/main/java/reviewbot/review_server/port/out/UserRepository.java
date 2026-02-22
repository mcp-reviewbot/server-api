package reviewbot.review_server.port.out;

import org.springframework.data.jpa.repository.JpaRepository;
import reviewbot.review_server.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
}