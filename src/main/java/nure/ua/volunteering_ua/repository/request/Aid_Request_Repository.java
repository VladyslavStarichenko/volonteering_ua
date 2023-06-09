package nure.ua.volunteering_ua.repository.request;


import nure.ua.volunteering_ua.model.Aid_Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Aid_Request_Repository extends PagingAndSortingRepository<Aid_Request, Long> {

  @Query(value = "SELECT * FROM request r WHERE r.organization_id IN (SELECT o.id FROM organization o WHERE o.name =?) AND r.queue_number = -1", nativeQuery = true)
  Page<Aid_Request> getAid_RequestByOrganization_Name(Pageable pageable, String organizationName);

  @Query(value = "SELECT * FROM request r WHERE r.organization_id IN (SELECT ov.organization_id FROM organization_volunteer ov WHERE ov.volunteer_id =?) AND r.queue_number != -1", nativeQuery = true)
  Page<Aid_Request> getAid_RequestByVolunteer(Pageable pageable, Long volunteerId);

  List<Aid_Request> getAllByOrganization_Name(String organizationName);

  @Query(value = "SELECT * FROM request", nativeQuery = true)
  List<Aid_Request> getAllRequest();

  @Query(value = "SELECT * FROM request ", nativeQuery = true)
  Page<Aid_Request> getAll(Pageable pageable);

  Page<Aid_Request> getAid_RequestByCustomer_Id(Pageable pageable,Long customerId);

  @Modifying
  @Transactional
  @Query(value = "update request set confirmation_code=? where id=?", nativeQuery = true)
  void update(int code, Long id);

  @Modifying
  @Transactional
  @Query(value = "update request set queue_number=? where id=?", nativeQuery = true)
  void updateQueue (int queue, Long id);
}
