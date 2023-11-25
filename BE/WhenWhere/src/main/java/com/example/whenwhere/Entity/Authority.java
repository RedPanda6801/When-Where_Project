package com.example.whenwhere.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Id
    @Column(name="authority_name")
    private Integer authorityName;
}
