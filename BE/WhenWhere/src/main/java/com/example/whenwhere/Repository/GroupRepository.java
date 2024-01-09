package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.Group;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("SELECT g.id FROM Group g WHERE g.groupName = :groupName")
    Optional<Integer> findByGroupName(@Param("groupName") String groupName);
}
