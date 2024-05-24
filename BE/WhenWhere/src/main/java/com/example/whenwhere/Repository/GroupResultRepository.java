package com.example.whenwhere.Repository;

import com.example.whenwhere.Dto.GroupResultDto;
import com.example.whenwhere.Entity.GroupResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupResultRepository extends JpaRepository<GroupResult, Integer> {
    @Query("SELECT gr FROM GroupResult gr WHERE gr.group.id = :groupId")
    Optional<GroupResult> findByGroupId(@Param("groupId") Integer groupId);
}
