package com.mercury.discovery.organization.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "client_department")
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Integer id;

    @Column
    Integer parentId;

    @Column(length = 100, nullable = false)
    String name;

    @Column
    Integer sort;

    @Column(columnDefinition = "tinyint(1) default 0")
    boolean isUse;

    @Column(nullable = false)
    Integer createdBy;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column
    Integer updatedBy;

    @Column
    LocalDateTime updatedAt;

    @Column(length = 36, unique = true, nullable = false)
    String departmentKey;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NonNull
    Set<User> users = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @NonNull
    @JoinColumn(name = "client_id")
    Client client;

    @PrePersist
    public void prePersist() {
        this.sort = this.sort == null ? 0 : this.sort;
    }
}
