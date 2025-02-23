package com.laborganized.LabOrganized.IT;

import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.config.DataLoader;
import com.laborganized.LabOrganized.repositories.ReagentRepository;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class ReagentIT {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    ReagentRepository reagentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DataLoader dataLoader;

    private final String URI = "/api/v1/reagents";

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
    void shouldFindAllReagents() {
        ResponseEntity<ReagentDTO[]> response = restTemplate.getForEntity(URI, ReagentDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldFindReagentByIdWhenValid() {
        Long id = reagentRepository.findByName("Interleukin-4").getId();
        ResponseEntity<ReagentDTO> response = restTemplate.getForEntity(URI + "/" + id, ReagentDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldNotFindReagentByIdWhenInvalid() {
        ResponseEntity<String> response = restTemplate.getForEntity(URI + "/9999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Reagent not found");
    }

    @Test
    void shouldSaveReagentWhenValid() {
        long initialReagentCount = reagentRepository.count();
        Long userId = reagentRepository.findByName("Interleukin-4").getUser().getId();
        Long containerId = reagentRepository.findByName("Interleukin-4").getContainer().getId();


        ReagentCreateRequest reagentCreateRequest = new ReagentCreateRequest(
                "Trypsin",
                userId,
                containerId,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ResponseEntity<ReagentDTO> response = restTemplate.postForEntity(URI, reagentCreateRequest, ReagentDTO.class);

        assertThat(response.getBody().getName()).isEqualTo("Trypsin");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount + 1);
    }

    @Test
    void shouldDeleteReagentWhenValid() {
        long initialReagentCount = reagentRepository.count();
        Long id = reagentRepository.findByName("Interleukin-4").getId();

        //Reagents and Containers share id generation because they both extend Storeable abstract class
        //We need to use id=4 because id=1 to id=3 are containers
        ResponseEntity<String> response = restTemplate.exchange(URI + "/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(response.getBody()).isEqualTo("Reagent deleted successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount - 1);
    }

    @Test
    void shouldUpdateReagentWhenValid() {
        long initialReagentCount = reagentRepository.count();
        Long id = reagentRepository.findByName("Interleukin-4").getId();

        ReagentDTO reagentDTO = new ReagentDTO();
        reagentDTO.setId(id);
        reagentDTO.setName("Updated reagent");

        HttpEntity<ReagentDTO> request = new HttpEntity<>(reagentDTO);

        ResponseEntity<ReagentDTO> response = restTemplate.exchange(URI, HttpMethod.PUT, request, ReagentDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated reagent");
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount);
    }
}
