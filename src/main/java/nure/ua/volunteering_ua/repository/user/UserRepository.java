package nure.ua.volunteering_ua.repository.user;

import nure.ua.volunteering_ua.model.System_Status;
import nure.ua.volunteering_ua.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {

    Optional<User> findUserByUserName(String name);

    boolean existsByUserName(String userName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users  SET status = ? WHERE user_name = ?", nativeQuery = true)
    void updateUserStatus(String status, String userName);

    @Query(value = "SELECT * FROM users  WHERE user_name  LIKE %?1%", nativeQuery = true)
    Page<User> searchUserByName(Pageable pageable, String userName);

}
