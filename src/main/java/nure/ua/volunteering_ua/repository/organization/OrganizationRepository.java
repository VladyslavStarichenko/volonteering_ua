package nure.ua.volunteering_ua.repository.organization;


import nure.ua.volunteering_ua.model.user.Organization;
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
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

  @Query(value = " FROM Organization o WHERE o.name = :name")
  Optional<Organization> getOrganizationByName(@Param("name") String name);

  @Query(value = " FROM Organization o WHERE o.name = :name")
  List<Optional<Organization>> getOrganizationByNameList(@Param("name") String name);

  @Query(value = "SELECT * FROM organization o WHERE o.volunteering_type = ?", nativeQuery = true)
  List<Organization> getAllByVolunteering_type(String volunteering_type);

  @Query(value = "SELECT * FROM organization o WHERE o.id IN" +
          " (SELECT ov.organization_id FROM organization_volunteer ov WHERE ov.volunteer_id IN(" +
          "SELECT volunteer_id FROM volunteer WHERE user_id =?)) ", nativeQuery = true)
  List<Organization> getAllByVolunteer(UUID userVolunteerId);


  @Query(value = "SELECT * FROM organization o WHERE o.id IN" +
          " (SELECT s.organization_id FROM subscription s WHERE s.customer_id IN(" +
          "SELECT customer_id FROM customer WHERE user_id =?)) ", nativeQuery = true)
  List<Organization> getAllByCustomer(UUID userVolunteerId);

  Page<Organization> findAll(Pageable pageable);

  @Query(value = " FROM Organization o WHERE o.organization_admin.id = :id")
  Optional<Organization> findAllByOrg_admin(@Param("id") UUID id);

  boolean existsByName(String name);

  @Modifying
  @Transactional
  @Query(value = "update organization set stripe_api_key=?, stripe_public_key=?, stripe_secret_key=? where id=?", nativeQuery = true)
  void updatePaymentMethod(String stripe_api_key, String stripe_public_key, String stripe_secret_key, Long id);


//  @Query(value = "SELECT * FROM organization o WHERE o.name LIKE %?%", nativeQuery = true)
//  List<Optional<Organization>> searchOrganizationByName(String organizationName);

  @Query(value = "SELECT * FROM organization  WHERE name  LIKE %?1%", nativeQuery = true)
  Page<Organization> searchOrganizationByNameIsIgnoreCase(Pageable pageable, String name);

}
