package com.laborganized.LabOrganized.IT;

import com.laborganized.LabOrganized.DTOs.UserCreateRequest;
import com.laborganized.LabOrganized.DTOs.UserDTO;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    UserRepository userRepository;

    @Test
    void shouldFindAllUsers() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity("/api/v1/users", UserDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldSaveUserWhenValid() {
        long initialUserCount = userRepository.count();

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "lluke",
                "Lucky Luke",
                "passwordHash",
                "lluke@gmail.com"
        );

        ResponseEntity<UserDTO> response = restTemplate.postForEntity("/api/v1/users", userCreateRequest, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Lucky Luke");
        assertThat(userRepository.count()).isEqualTo(initialUserCount + 1);
    }


}
