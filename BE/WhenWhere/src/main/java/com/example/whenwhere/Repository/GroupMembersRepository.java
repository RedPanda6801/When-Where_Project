package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.GroupMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Integer> {
    List<GroupMembers> findAllByGroupId(Integer groupId);
}
