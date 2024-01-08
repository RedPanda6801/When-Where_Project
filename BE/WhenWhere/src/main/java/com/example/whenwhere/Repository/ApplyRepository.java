package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.Apply;
import com.example.whenwhere.Entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Integer> {
    @Query("SELECT a.id FROM Apply a WHERE a.applier.id = :applierId AND a.group.id = :groupId")
    List<Integer> findApplierInGroup(@Param("applierId") Integer applierId, @Param("groupId") Integer groupId);

    @Query("SELECT a.id, a.accepted, a.state FROM Apply a WHERE a.applier.id = :userId")
    List<Object> findAllByUserId(@Param("userId") Integer userId);
}
