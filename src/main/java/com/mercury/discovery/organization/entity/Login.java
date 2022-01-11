package com.mercury.discovery.organization.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "client_user_login")
@Builder
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Integer id;

    @Column(length = 100, nullable = false)
    String username;

    @Column(length = 100, nullable = false)
    String password;

    @Column
    Integer passwordErrCount;

    @Column(nullable = false)
    LocalDateTime passwordUpdatedAt;

    @Column
    LocalDateTime lastLoginAt;

    @Column
    LocalDateTime lastLogoutAt;

    @Column(length = 40)
    String lastIpAddress;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
