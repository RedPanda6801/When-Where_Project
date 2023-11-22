package com.example.whenwhere.Entity;

import com.example.whenwhere.Dto.ScheduleDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", length = 20)
    private String title;

    @Column(name="detail", length = 200)
    private String detail;

    @Column(name="start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 끝 시간이 자정이 넘어갈 수 있기에 날짜까지 받아줌
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="end_time")
    private LocalDateTime endTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    public Schedule toEntity(ScheduleDto dto){
        return Schedule.builder()
                .title(dto.getTitle())
                .detail(dto.getDetail())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }
}
