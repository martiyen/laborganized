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
import org.springframework.http.*;

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
    void shouldFindAllReagents_WhenAuthenticatedAndRoleIsADMIN() {
        ResponseEntity<String> auth = restTemplate.withBasicAuth("admin", "password").postForEntity("/token", RequestEntity.EMPTY, String.class);
        token = auth.getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<ReagentDTO[]> response = restTemplate.exchange(URI + "/all", HttpMethod.GET, request, ReagentDTO[].class);

        assertThat(response.getBody().length).isEqualTo(3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotFindAllReagents_WhenAuthenticatedAndRoleIsNotADMIN() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<ReagentDTO[]> response = restTemplate.exchange(URI + "/all", HttpMethod.GET, request, ReagentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldFindReagentById_WhenValid_WhenAuthenticated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        Long id = reagentRepository.findByName("Interleukin-4").getId();
        ResponseEntity<ReagentDTO> response = restTemplate.exchange(URI + "/" + id, HttpMethod.GET, request, ReagentDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldNotFindReagentById_WhenInvalid_WhenAuthenticated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(URI + "/9999", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Reagent not found");
    }

    @Test
    void shouldFindAllUserReagents_WhenAuthenticated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<ReagentDTO[]> response = restTemplate.exchange(URI, HttpMethod.GET, request, ReagentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(2);
    }

    @Test
    void shouldSaveReagent_WhenValid_WhenAuthenticated() {
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<ReagentCreateRequest> request = new HttpEntity<>(reagentCreateRequest, headers);

        ResponseEntity<ReagentDTO> response = restTemplate.postForEntity(URI, request, ReagentDTO.class);

        assertThat(response.getBody().getName()).isEqualTo("Trypsin");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount + 1);
    }

    @Test
    void shouldDeleteReagent_WhenValid_WhenAuthenticated() {
        long initialReagentCount = reagentRepository.count();
        Long id = reagentRepository.findByName("Interleukin-4").getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>("", headers);

        //Reagents and Containers share id generation because they both extend Storeable abstract class
        //We need to use id=4 because id=1 to id=3 are containers
        ResponseEntity<String> response = restTemplate.exchange(URI + "/" + id, HttpMethod.DELETE, request, String.class);

        assertThat(response.getBody()).isEqualTo("Reagent deleted successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount - 1);
    }

    @Test
    void shouldUpdateReagent_WhenValid_WhenAuthenticated() {
        long initialReagentCount = reagentRepository.count();
        Long id = reagentRepository.findByName("Interleukin-4").getId();

        ReagentDTO reagentDTO = new ReagentDTO();
        reagentDTO.setId(id);
        reagentDTO.setName("Updated reagent");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<ReagentDTO> request = new HttpEntity<>(reagentDTO, headers);

        ResponseEntity<ReagentDTO> response = restTemplate.exchange(URI, HttpMethod.PUT, request, ReagentDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated reagent");
        assertThat(reagentRepository.count()).isEqualTo(initialReagentCount);
    }
}
