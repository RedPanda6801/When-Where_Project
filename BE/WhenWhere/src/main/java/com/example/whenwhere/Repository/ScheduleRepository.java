package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT s FROM User u LEFT JOIN u.schedules s WHERE s.user.id in :memberIds AND" +
            " DATE(s.startTime) BETWEEN :startDate AND :endDate ORDER BY s.startTime, s.endTime")
    List<Schedule> findSortedScheduleByDates(List<Integer> memberIds, Date startDate, Date endDate);
}
