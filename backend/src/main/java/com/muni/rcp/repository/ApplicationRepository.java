package com.muni.rcp.repository;

import com.muni.rcp.entity.ApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String> {

    Optional<ApplicationEntity> findByTenantIdAndApplicationNumber(String tenantId, String applicationNumber);

    @Query("""
        SELECT a FROM ApplicationEntity a
        WHERE a.tenantId = :tenantId
          AND (:applicationNumber IS NULL OR a.applicationNumber = :applicationNumber)
          AND (:status IS NULL OR a.status = :status)
          AND (:applicantMobile IS NULL OR a.applicantMobile = :applicantMobile)
        ORDER BY a.createdTime DESC
    """)
    Page<ApplicationEntity> searchApplications(
            @Param("tenantId") String tenantId,
            @Param("applicationNumber") String applicationNumber,
            @Param("status") String status,
            @Param("applicantMobile") String applicantMobile,
            Pageable pageable
    );
}
