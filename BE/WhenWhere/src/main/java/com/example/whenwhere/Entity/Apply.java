package com.example.whenwhere.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apply")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Apply {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 처리 상태에 대해 지원자도 알아야 한다.
    @Column(name="state")
    private Boolean state;

    @Column(name="accepted")
    private Boolean accepted;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="applier_id")
    private User applier;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="group_id")
    private Group group;
}
