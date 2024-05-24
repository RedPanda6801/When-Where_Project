package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.GroupResultDto;
import com.example.whenwhere.Entity.Group;
import com.example.whenwhere.Entity.GroupResult;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.GroupRepository;
import com.example.whenwhere.Repository.GroupResultRepository;
import com.example.whenwhere.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class GroupResultService {

    private final GroupRepository groupRepository;
    private final GroupResultRepository groupResultRepository;

    public GroupResultService(GroupRepository groupRepository,
                              GroupResultRepository groupResultRepository) {
        this.groupRepository = groupRepository;
        this.groupResultRepository = groupResultRepository;
    }

    @Transactional
    public void addResult(GroupResultDto dto, String userId){
        try{
            Optional<Group> groupOptional = groupRepository.findById(dto.getGroupId());
            if(groupOptional.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
            }
            Group group = groupOptional.get();
            // 그룹에 속한 결과는 하나여야 함
            Optional<GroupResult> groupResultOptional = groupResultRepository.findByGroupId(group.getId());
            if(groupResultOptional.isPresent()){
                GroupResult deleteResult = groupResultOptional.get();
                deleteResult.setGroup(null);
                groupResultRepository.delete(groupResultOptional.get());
            }
            System.out.println("삭제?" +groupResultRepository.findByGroupId(group.getId()).isPresent());
            GroupResult newResult = new GroupResult();
            newResult = newResult.toEntity(dto, group);

            // 이전 결과 삭제 후에 새로운 결과 저장
            groupResultRepository.save(newResult);
        }catch (Exception e){
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    public GroupResultDto getResult(Integer groupId, String userId){
        try{
            Optional<GroupResult> groupResultOptional = groupResultRepository.findByGroupId(groupId);
            if(groupResultOptional.isEmpty()){
                return new GroupResultDto();
            }
            GroupResult groupResult = groupResultOptional.get();
            return GroupResultDto.toDto(groupResult);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

}
