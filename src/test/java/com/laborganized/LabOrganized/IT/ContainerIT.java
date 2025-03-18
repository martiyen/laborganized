package com.laborganized.LabOrganized.IT;

import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.config.DataLoader;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class ContainerIT {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DataLoader dataLoader;

    private final String URI = "/api/v1/containers";
    private String token;

    @BeforeEach
    void resetDatabase() {
        userRepository.deleteAll();
        try {
            dataLoader.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        ResponseEntity<String> auth = restTemplate.withBasicAuth("jdoe", "password").postForEntity("/token", RequestEntity.EMPTY, String.class);
        token = auth.getBody();
    }

    @Test
    void shouldFindAllContainers_WhenAuthenticatedAndRoleIsADMIN() {
        ResponseEntity<String> auth = restTemplate.withBasicAuth("admin", "password").postForEntity("/token", RequestEntity.EMPTY, String.class);
        token = auth.getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<ContainerDTO[]> response = restTemplate.exchange(URI + "/all", HttpMethod.GET, request, ContainerDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotFindAllContainers_WhenAuthenticatedAndRoleIsNotADMIN() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<ContainerDTO[]> response = restTemplate.exchange(URI + "/all", HttpMethod.GET, request, ContainerDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldFindContainerById_WhenValidId_WhenAuthenticated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        Long id = containerRepository.findByName("Freezer A").getId();
        ResponseEntity<ContainerDTO> response = restTemplate.exchange(URI + "/" + id, HttpMethod.GET, request, ContainerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldNotFindContainerById_WhenInvalidId_WhenAuthenticated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(URI + "/9999", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Container not found");
    }

    @Test
    void shouldFindAllUserContainers_WhenAuthenticated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<ContainerDTO[]> response = restTemplate.exchange(URI, HttpMethod.GET, request, ContainerDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(2);
    }

    @Test
    void shouldSaveContainer_WhenValid_WhenAuthenticated() {
        long initialContainerCount = containerRepository.count();
        Long userId = containerRepository.findByName("Freezer A").getUser().getId();

        ContainerCreateRequest containerCreateRequest = new ContainerCreateRequest(
                "Fridge",
                -80.0,
                null,
                userId,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<ContainerCreateRequest> request = new HttpEntity<>(containerCreateRequest, headers);

        ResponseEntity<ContainerDTO> response = restTemplate.postForEntity(URI, request, ContainerDTO.class);

        assertThat(response.getBody().getName()).isEqualTo("Fridge");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount + 1);
    }

    @Test
    void shouldDeleteContainer_WhenValid_WhenAuthenticated() {
        long initialContainerCount = containerRepository.count();
        Long id = containerRepository.findByName("Freezer A").getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(URI + "/" + id, HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Container deleted successfully");
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount - 2);
        //We expect 2 containers to be removed because deleted container contained one other container
        //And Cascade.Type property was set to REMOVE
    }

    @Test
    void shouldUpdateContainer_WhenValid_WhenAuthenticated() {
        long initialContainerCount = containerRepository.count();
        Long id = containerRepository.findByName("Freezer A").getId();

        ContainerDTO container = new ContainerDTO();
        container.setId(id);
        container.setName("Updated container");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<ContainerDTO> request = new HttpEntity<>(container, headers);

        ResponseEntity<ContainerDTO> response = restTemplate.exchange(URI, HttpMethod.PUT, request, ContainerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated container");
        assertThat(response.getBody().getCapacity()).isEqualTo(null);
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount);
    }
}
