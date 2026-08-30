package com.muni.rcp.repository;

import com.muni.rcp.entity.ActionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionHistoryRepository extends JpaRepository<ActionHistoryEntity, String> {

    List<ActionHistoryEntity> findByTenantIdAndApplicationIdOrderByCreatedTimeAsc(String tenantId, String applicationId);
}
