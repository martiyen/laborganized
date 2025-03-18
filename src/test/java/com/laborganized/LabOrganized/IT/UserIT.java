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
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        try {
            dataLoader.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ResponseEntity<String> auth = restTemplate.withBasicAuth("admin", "password").postForEntity("/token", RequestEntity.EMPTY, String.class);
        token = auth.getBody();
    }

    @Test
    void shouldFindAllUsers_WhenAuthenticatedAndRoleIsADMIN() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<UserDTO[]> response = restTemplate.exchange(URI, HttpMethod.GET, request, UserDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotFindAllUsers_WhenAuthenticatedAndRoleIsNotADMIN() {
        ResponseEntity<String> auth = restTemplate.withBasicAuth("jdoe", "password").postForEntity("/token", RequestEntity.EMPTY, String.class);
        token = auth.getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<UserDTO[]> response = restTemplate.exchange(URI, HttpMethod.GET, request, UserDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldFindUserById_WhenValidId_WhenAuthenticatedAndRoleIsADMIN() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        Long id = userRepository.findByUsername("admin").orElseThrow().getId();
        ResponseEntity<UserDTO> response = restTemplate.exchange(URI + "/" + id, HttpMethod.GET, request, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldNotFindUserById_WhenInvalidId_WhenAuthenticatedAndRoleIsADMIN() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(URI + "/9999", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void shouldSaveUser_WhenValid_WhenAuthenticatedAndRoleIsADMIN() {
        long initialUserCount = userRepository.count();

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "lluke",
                "Lucky Luke",
                "password_hash",
                "lluke@gmail.com"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<UserCreateRequest> request = new HttpEntity<>(userCreateRequest, headers);

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(URI, request, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Lucky Luke");
        assertThat(userRepository.count()).isEqualTo(initialUserCount + 1);
    }

    @Test
    void shouldDeleteUser_WhenValidId_WhenAuthenticatedAndRoleIsADMIN() {
        long initialUserCount = userRepository.count();
        Long id = userRepository.findByUsername("admin").orElseThrow().getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(URI + "/" + id, HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.count()).isEqualTo(initialUserCount - 1);
        assertThat(response.getBody()).isEqualTo("User deleted successfully");
    }

    @Test
    void shouldNotDeleteUser_WhenIdNotValid_WhenAuthenticatedAndRoleIsADMIN() {
        long initialUserCount = userRepository.count();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(URI + "/9999", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(userRepository.count()).isEqualTo(initialUserCount);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void shouldUpdateUser_WhenValid_WhenAuthenticatedAndRoleIsADMIN() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        Long id = userRepository.findByUsername("admin").orElseThrow().getId();
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setEmail("updated@example.com");
        HttpEntity<UserDTO> request = new HttpEntity<>(user, headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(URI, HttpMethod.PUT, request, UserDTO.class);

        assertThat(response.getBody().getEmail()).isEqualTo("updated@example.com");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
