package com.example.whenwhere.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", length=20, unique = true)
    private String userId;

    @Column(name="password", length = 100)
    private String password;

    @Column(name="nickname", length=50)
    private String nickname;

    @Column(name="location", length = 100)
    private String location;

    @JsonIgnore
    @Column(name="activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name="authority_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities;
}
