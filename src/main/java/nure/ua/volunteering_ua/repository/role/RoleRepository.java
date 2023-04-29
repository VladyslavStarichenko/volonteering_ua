package nure.ua.volunteering_ua.repository.role;



import nure.ua.volunteering_ua.model.user.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role,Long> {
    Role findByName(String name);
}
