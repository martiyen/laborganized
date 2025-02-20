package com.laborganized.LabOrganized.IT;

import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.repositories.ReagentRepository;
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
public class ReagentIT {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    ReagentRepository reagentRepository;

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @Test
    void shouldFindAllReagents() {
        ResponseEntity<ReagentDTO[]> response = restTemplate.getForEntity("/api/v1/reagents", ReagentDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldSaveReagentWhenValid() {
        long initialReagentCount = reagentRepository.count();

        ReagentCreateRequest reagentCreateRequest = new ReagentCreateRequest(
                "Trypsin",
                1L,
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ResponseEntity<ReagentDTO> response = restTemplate.postForEntity("/api/v1/reagents", reagentCreateRequest, ReagentDTO.class);

        assertThat(response.getBody().getName()).isEqualTo("Trypsin");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount + 1);
    }
}
