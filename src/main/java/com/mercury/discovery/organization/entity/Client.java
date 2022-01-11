package com.mercury.discovery.organization.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * https://jinhokwon.github.io/springboot/springboot-jpa-one-to-many/
 * https://medium.com/chequer/jpa-hibernate-hbm2ddl-%EC%BB%AC%EB%9F%BC-%EC%88%9C%EC%84%9C-%EC%A7%80%EC%A0%95%ED%95%98%EA%B8%B0-e0c4421a8d32
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "client")
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Integer id;

    @Column(length = 15, nullable = false)
    String name;

    @Column(length = 15, nullable = false)
    String engName;

    @Column(length = 15, nullable = false)
    String status;

    @Column(length = 15, nullable = false)
    String industryCode;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Column(length = 36, unique = true, nullable = false)
    String clientKey;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Department> departments = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
        this.updatedAt = this.updatedAt == null ? LocalDateTime.now() : this.updatedAt;
        this.clientKey = this.clientKey == null ? "asfkjasdlvnsldlsdnv" : this.clientKey;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", engName='" + engName + '\'' +
                ", status='" + status + '\'' +
                ", industryCode='" + industryCode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", clientKey='" + clientKey + '\'' +
                '}';
    }
}
