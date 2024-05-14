package com.example.whenwhere.Repository;

import com.example.whenwhere.Dto.GroupDto;
import com.example.whenwhere.Entity.GroupMembers;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Integer> {
    @Query("SELECT new com.example.whenwhere.Dto.GroupDto(g.id, g.groupName, g.attribute) FROM GroupMembers m JOIN Group g ON m.group.id = g.id WHERE m.user.id = :userPk")
    List<GroupDto> findAllByUserPk(@Param("userPk") Integer userPk);

    @Query("SELECT m FROM GroupMembers m WHERE m.group.id = :groupId")
    List<GroupMembers> findAllByGroupId(@Param("groupId") Integer groupId);

    @Modifying
    @Query("DELETE FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    void deleteMember(@Param("groupId") Integer groupId, @Param("userId") Integer userId);
}
