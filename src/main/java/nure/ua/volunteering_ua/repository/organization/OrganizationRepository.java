package nure.ua.volunteering_ua.repository.organization;


import nure.ua.volunteering_ua.model.user.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

  @Query(value = " FROM Organization o WHERE o.name = :name")
  Optional<Organization> getOrganizationByName(@Param("name") String name);

  @Query(value = "SELECT * FROM organization o WHERE o.volunteering_type = ?", nativeQuery = true)
  List<Organization> getAllByVolunteering_type(int volunteering_type);

  @Query(value = "SELECT * FROM organization o WHERE o.id IN" +
          " (SELECT ov.organization_id FROM organization_volunteers ov WHERE ov.volunteer_id IN(" +
          "SELECT volunteer_id FROM ov WHERE user_id == ?)) ", nativeQuery = true)
  List<Organization> getAllByVolunteer(UUID userVolunteerId);

  Page<Organization> findAll(Pageable pageable);

  @Query(value = " FROM Organization o WHERE o.organization_admin.id = :id")
  Optional<Organization> findAllByOrg_admin(@Param("id") UUID id);

  boolean existsByName(String name);


}
