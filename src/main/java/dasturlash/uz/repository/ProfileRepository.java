package dasturlash.uz.repository;

import dasturlash.uz.entity.Profile;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<Profile, String> {
    Optional<Profile> findByEmailAndVisibleTrue(String username);

    Optional<Profile> findByIdAndVisibleTrue(Long id);

    boolean existsByEmailAndVisibleTrue(String email);
}
