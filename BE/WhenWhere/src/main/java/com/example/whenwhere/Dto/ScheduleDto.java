package com.example.whenwhere.Dto;
import com.example.whenwhere.Entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ScheduleDto {

    private Integer id;

    private String title;

    private String detail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public static ScheduleDto toDto(Schedule entity){
        return ScheduleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .detail(entity.getDetail())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }
}
