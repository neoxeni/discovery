package com.mercury.discovery.organization.entity;

import com.mercury.discovery.organization.entity.Client;
import com.mercury.discovery.organization.entity.Department;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "client_user")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Integer id;

    @Column(length = 100, nullable = false)
    String name;

    @Column(length = 100, nullable = false)
    String nickname;

    @Column(length = 15)
    String phone;

    @Column(length = 50)
    String email;

    @Column(length = 20)
    String identification;

    @Column(length = 15)
    String extensionNo;

    @Column(length = 36)
    String positionCd;

    @Column(length = 36)
    String dutyCd;

    @Column
    Integer sort;

    @Column(length = 10)
    String status;

    @Column
    LocalDate joinDate;

    @Column(columnDefinition = "tinyint(1) default 0")
    boolean isRetire;

    @Column
    LocalDate retireDate;

    @Column(nullable = false)
    Integer createdBy;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column
    Integer updatedBy;

    @Column
    LocalDateTime updatedAt;

    @Column(length = 36, unique = true, nullable = false)
    String userKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @NonNull
    @JoinColumn(name = "client_id")
    Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @NonNull
    @JoinColumn(name = "department_id")
    Department department;

    @PrePersist
    public void prePersist() {
        this.sort = this.sort == null ? 0 : this.sort;
    }
}
