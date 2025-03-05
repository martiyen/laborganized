package com.laborganized.LabOrganized.config;

import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.Reagent;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.ReagentRepository;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    ReagentRepository reagentRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setName("Administrator");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setEmail("admin@example.com");
            admin.setCreated(LocalDateTime.now());
            admin.setLastUpdated(admin.getCreated());
            admin.setRoles("ROLE_ADMIN,ROLE_MANAGER,ROLE_MEMBER");

            userRepository.save(admin);

            User jdoe = new User();
            jdoe.setUsername("jdoe");
            jdoe.setName("John Doe");
            jdoe.setPasswordHash(passwordEncoder.encode("password"));
            jdoe.setEmail("jdoe@example.com");
            jdoe.setCreated(LocalDateTime.now());
            jdoe.setLastUpdated(jdoe.getCreated());
            jdoe.setRoles("ROLE_MANAGER,ROLE_MEMBER");

            userRepository.save(jdoe);

            User asmith = new User();
            asmith.setUsername("asmith");
            asmith.setName("Alice Smith");
            asmith.setPasswordHash(passwordEncoder.encode("password"));
            asmith.setEmail("asmith@example.com");
            asmith.setCreated(LocalDateTime.now());
            asmith.setLastUpdated(asmith.getCreated());
            asmith.setRoles("ROLE_MANAGER,ROLE_MEMBER");

            userRepository.save(asmith);

            Container freezerA = new Container();
            freezerA.setName("Freezer A");
            freezerA.setUser(jdoe);
            freezerA.setTemperature(-80.0);

            containerRepository.save(freezerA);

            Container box1 = new Container();
            box1.setName("Box 1");
            box1.setUser(jdoe);
            box1.setContainer(freezerA);
            box1.setCapacity(81);
            box1.setTemperature(freezerA.getTemperature());

            containerRepository.save(box1);

            Container box2 = new Container();
            box2.setName("Box 2");
            box2.setUser(asmith);
            box2.setTemperature(25.0);

            containerRepository.save(box2);

            Reagent interleukin4 = new Reagent();
            interleukin4.setName("Interleukin-4");
            interleukin4.setUser(jdoe);
            interleukin4.setContainer(box1);
            interleukin4.setQuantity(1000.0);
            interleukin4.setUnit("µL");
            interleukin4.setSupplier("PROLEUKIN");
            interleukin4.setReference("110-80-250");
            interleukin4.setConcentration("10⁵ U/mL");
            interleukin4.setExpirationDate(LocalDate.now().plusYears(3));
            interleukin4.setComments("Activity was measured by J.Doe");

            reagentRepository.save(interleukin4);

            Reagent gmcsf = new Reagent();
            gmcsf.setName("GM-CSF");
            gmcsf.setUser(jdoe);
            gmcsf.setContainer(box1);
            gmcsf.setQuantity(1000.0);
            gmcsf.setUnit("µL");
            gmcsf.setSupplier("PROLEUKIN");
            gmcsf.setReference("114-70-250");
            gmcsf.setConcentration("10 µg/mL");
            gmcsf.setExpirationDate(LocalDate.now().plusYears(3));
            gmcsf.setComments("Aliquoted by J.Doe");

            reagentRepository.save(gmcsf);

            Reagent ethanol = new Reagent();
            ethanol.setName("Ethanol");
            ethanol.setUser(asmith);
            ethanol.setContainer(box2);
            ethanol.setQuantity(5.0);
            ethanol.setUnit("L");
            ethanol.setSupplier("BD");
            ethanol.setReference("444-5000");
            ethanol.setConcentration("99%");
            ethanol.setComments("Please ask J.Doe before use.");

            reagentRepository.save(ethanol);
        }
    }
}
