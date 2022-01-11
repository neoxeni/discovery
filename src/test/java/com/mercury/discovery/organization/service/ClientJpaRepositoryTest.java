package com.mercury.discovery.organization.service;

import com.mercury.discovery.config.database.DatabaseConfig;
import com.mercury.discovery.organization.entity.Client;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@ActiveProfiles("test")
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
