package com.devbhaskar.gsd_kafka.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devbhaskar.gsd_kafka.model.AlarmList;

import jakarta.transaction.Transactional;

@Repository
public interface AlarmListRepository extends JpaRepository<AlarmList, Long> {

    // Example custom finders:
    Optional<AlarmList> findByAlarmUniqueId(String alarmUniqueId);

    List<AlarmList> findByIsAlarmOpen(boolean isAlarmOpen);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM AlarmList a WHERE LOWER(a.alarmUniqueId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    int deleteByKeyword(@Param("keyword") String keyword);
}
