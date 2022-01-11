package com.mercury.discovery.organization.service;

import com.mercury.discovery.organization.entity.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ClientJpaRepositoryTest {
    @Autowired
    private ClientJpaRepository clientJpaRepository;

    @Test
    public void insert() {
        Client client = Client.builder()
                .name("머큐리프로젝트")
                .engName("MERCURY")
                .status("ACTIVE")
                .industryCode("HOUSING")
                .build();

        clientJpaRepository.save(client);
        System.out.println(client);
    }
}
