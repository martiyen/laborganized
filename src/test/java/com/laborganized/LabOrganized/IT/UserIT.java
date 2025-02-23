package com.laborganized.LabOrganized.IT;

import com.laborganized.LabOrganized.DTOs.UserCreateRequest;
import com.laborganized.LabOrganized.DTOs.UserDTO;
import com.laborganized.LabOrganized.config.DataLoader;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class UserIT {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DataLoader dataLoader;

    private final String URI = "/api/v1/users";

    @BeforeEach
    void resetDatabase() {
        userRepository.deleteAll();
        try {
            dataLoader.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldFindAllUsers() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(URI, UserDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldFindUserByIdWhenValidId() {
        Long id = userRepository.findByUsername("admin").getId();
        ResponseEntity<UserDTO> response = restTemplate.getForEntity(URI + "/" + id, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldNotFindUserByIdWhenInvalidId() {
        ResponseEntity<String> response = restTemplate.getForEntity(URI + "/9999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void shouldSaveUserWhenValid() {
        long initialUserCount = userRepository.count();

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "lluke",
                "Lucky Luke",
                "password_hash",
                "lluke@gmail.com"
        );

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(URI, userCreateRequest, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Lucky Luke");
        assertThat(userRepository.count()).isEqualTo(initialUserCount + 1);
    }

    @Test
    void shouldDeleteUserWhenValidId() {
        long initialUserCount = userRepository.count();
        Long id = userRepository.findByUsername("admin").getId();

        ResponseEntity<String> response = restTemplate.exchange(URI + "/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.count()).isEqualTo(initialUserCount - 1);
        assertThat(response.getBody()).isEqualTo("User deleted successfully");
    }

    @Test
    void shouldNotDeleteUserWhenIdNotValid() {
        long initialUserCount = userRepository.count();

        ResponseEntity<String> response = restTemplate.exchange(URI + "/9999", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(userRepository.count()).isEqualTo(initialUserCount);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void shouldUpdateUserWhenValid() {
        Long id = userRepository.findByUsername("admin").getId();
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setEmail("updated@example.com");
        HttpEntity<UserDTO> request = new HttpEntity<>(user);

        ResponseEntity<UserDTO> response = restTemplate.exchange(URI, HttpMethod.PUT, request, UserDTO.class);

        assertThat(response.getBody().getEmail()).isEqualTo("updated@example.com");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



}
