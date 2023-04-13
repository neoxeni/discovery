package com.mercury.discovery.base.users.service;

import com.mercury.discovery.base.users.model.DatabaseClientRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ClientRegistrationRepositoryImpl {

    DatabaseClientRegistration findById(Integer id);

    DatabaseClientRegistration findByRegistrationId(String registrationId);

    int insert(DatabaseClientRegistration databaseClientRegistration);

    int update(DatabaseClientRegistration databaseClientRegistration);

    int delete(Long clientId);
}
