package com.example.whenwhere.Repository;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.UserDto;
import com.example.whenwhere.Entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUserId(String userid);

    Optional<User> findByUserId(String userId);

    @Query("SELECT new com.example.whenwhere.Dto.ApplyDto(a.id, a.group.id, u.userId, u.nickname, a.state, a.accepted) FROM User u LEFT JOIN u.applies a WHERE a.group.id = :gid AND a.state = false")
    List<ApplyDto> findAllUserByGroupId(@Param("gid") Integer gid);

    @Query("SELECT new com.example.whenwhere.Dto.UserDto(u.id, u.userId, u.nickname) FROM User u LEFT JOIN u.groupMembers g WHERE g.group.id = :groupId")
    List<UserDto> findMembersByGroup(@Param("groupId")Integer groupId);
}
