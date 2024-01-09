package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.GroupMembers;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Integer> {
    @Query("SELECT g.id, g.groupName, g.attribute FROM GroupMembers m JOIN Group g ON m.group.id = g.id WHERE m.user.id = :userPk")
    List<Object> findAllByUserPk(@Param("userPk") Integer userPk);
}
