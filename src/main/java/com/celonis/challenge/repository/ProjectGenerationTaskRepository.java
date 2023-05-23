package com.celonis.challenge.repository;

import com.celonis.challenge.model.ProjectGenerationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {

    @Query("from ProjectGenerationTask p where p.storageLocation is null and p.creationDate <= :thresholdDate")
    List<ProjectGenerationTask> deleteUnExecutedTasksOlderThan(@Param("thresholdDate") Date thresholdDate);
}
