package com.muni.rcp.repository;

import com.muni.rcp.entity.ApplicationSequenceEntity;
import com.muni.rcp.entity.ApplicationSequenceId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationSequenceRepository extends JpaRepository<ApplicationSequenceEntity, ApplicationSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ApplicationSequenceEntity s WHERE s.tenantId = :tenantId AND s.financialYear = :financialYear")
    Optional<ApplicationSequenceEntity> findByTenantIdAndFinancialYearForUpdate(
            @Param("tenantId") String tenantId,
            @Param("financialYear") String financialYear
    );
}
