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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    void shouldFindAllContainers() {
        ResponseEntity<ContainerDTO[]> response = restTemplate.getForEntity(URI, ContainerDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldFindContainerByIdWhenValidId() {
        Long id = containerRepository.findByName("Freezer A").getId();
        ResponseEntity<ContainerDTO> response = restTemplate.getForEntity(URI + "/" + id, ContainerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldNotFindContainerByIdWhenInvalidId() {
        ResponseEntity<String> response = restTemplate.getForEntity(URI + "/9999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Container not found");
    }

    @Test
    void shouldSaveContainerWhenValid() {
        long initialContainerCount = containerRepository.count();
        Long userId = containerRepository.findByName("Freezer A").getUser().getId();

        ContainerCreateRequest containerCreateRequest = new ContainerCreateRequest(
                "Fridge",
                -80.0,
                null,
                userId,
                null
        );

        ResponseEntity<ContainerDTO> response = restTemplate.postForEntity(URI, containerCreateRequest, ContainerDTO.class);

        assertThat(response.getBody().getName()).isEqualTo("Fridge");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount + 1);
    }

    @Test
    void shouldDeleteContainerWhenValid() {
        long initialContainerCount = containerRepository.count();
        Long id = containerRepository.findByName("Freezer A").getId();

        ResponseEntity<String> response = restTemplate.exchange(URI + "/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Container deleted successfully");
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount - 2);
        //We expect 2 containers to be removed because deleted container contained one other container
        //And Cascade.Type property was set to REMOVE
    }

    @Test
    void shouldUpdateContainerWhenValid() {
        long initialContainerCount = containerRepository.count();
        Long id = containerRepository.findByName("Freezer A").getId();

        ContainerDTO container = new ContainerDTO();
        container.setId(id);
        container.setName("Updated container");

        HttpEntity<ContainerDTO> request = new HttpEntity<>(container);

        ResponseEntity<ContainerDTO> response = restTemplate.exchange(URI, HttpMethod.PUT, request, ContainerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated container");
        assertThat(response.getBody().getCapacity()).isEqualTo(null);
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount);
    }
}
