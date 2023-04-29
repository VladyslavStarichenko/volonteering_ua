package nure.ua.volunteering_ua.model;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    @CreationTimestamp
    @Column(name = "created")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated")
    private Date updatedAt;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private System_Status status;


}
