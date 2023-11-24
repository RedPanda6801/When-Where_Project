package com.example.whenwhere.Repository;

import com.example.whenwhere.Entity.Apply;
import com.example.whenwhere.Entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Integer> {
}
