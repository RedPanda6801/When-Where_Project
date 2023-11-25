package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserId(String userId);

    @Query("SELECT a.id, u.id, u.userId, u.nickname FROM User u LEFT JOIN u.applies a WHERE a.group.id = :gid AND a.state = false")
    List<Object> findAllUserByGroupId(@Param("gid") Integer gid);

    @Query("SELECT u.id, u.userId, u.nickname FROM User u LEFT JOIN u.groupMembers g WHERE g.group.id = :groupId AND g.group.host.id = :hostId")
    List<Object> findMembersByGroup(@Param("groupId")Integer groupId, @Param("hostId") Integer hostId);
}
