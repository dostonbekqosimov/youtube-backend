package dasturlash.uz.repository;

import dasturlash.uz.entity.Profile;
import dasturlash.uz.mapper.ProfileShortInfoMapper;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
    Optional<Profile> findByEmailAndVisibleTrue(String username);

    Optional<Profile> findByIdAndVisibleTrue(Long id);

    boolean existsByEmailAndVisibleTrue(String email);

    @Query("select p.id as id, p.name as name, p.surname as surname, p.email as email, p.photoId as photoId " +
            "from Profile p where p.email = ?1")
    ProfileShortInfoMapper getProfileShortInfoMapper(String id);

}
