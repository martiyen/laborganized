package com.laborganized.LabOrganized.IT;

import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContainerIT {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    ContainerRepository containerRepository;

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @Test
    void shouldFindAllContainers() {
        ResponseEntity<ContainerDTO[]> response = restTemplate.getForEntity("/api/v1/containers", ContainerDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldSaveContainerWhenValid() {
        long initialContainerCount = containerRepository.count();

        ContainerCreateRequest containerCreateRequest = new ContainerCreateRequest(
                "Fridge",
                -80.0,
                null,
                1L,
                null
        );

        ResponseEntity<ContainerDTO> response = restTemplate.postForEntity("/api/v1/containers", containerCreateRequest, ContainerDTO.class);

        assertThat(response.getBody().getName()).isEqualTo("Fridge");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(containerRepository.count()).isEqualTo(initialContainerCount + 1);
    }
}
