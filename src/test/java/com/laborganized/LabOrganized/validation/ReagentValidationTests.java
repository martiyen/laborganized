package com.laborganized.LabOrganized.validation;

import com.laborganized.LabOrganized.models.Reagent;
import com.laborganized.LabOrganized.models.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ReagentValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWhenValid() {
        Reagent reagent = getValidReagent();

        Set<ConstraintViolation<Reagent>> violations = validator.validate(reagent);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void shouldNotValidateWhenNameIsEmpty() {
        Reagent reagent = getValidReagent();
        reagent.setName(null);

        Set<ConstraintViolation<Reagent>> violations = validator.validate(reagent);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must not be empty");
    }

    @Test
    void shouldNotValidateWhenUserIsNull() {
        Reagent reagent = getValidReagent();
        reagent.setUser(null);

        Set<ConstraintViolation<Reagent>> violations = validator.validate(reagent);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User must not be null");
    }

    @Test
    void shouldNotValidateWhenQuantityIsNegative() {
        Reagent reagent = getValidReagent();
        reagent.setQuantity(-1.0);

        Set<ConstraintViolation<Reagent>> violations = validator.validate(reagent);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Quantity must not be negative");


    }

    private Reagent getValidReagent() {
        User user = new User();
        user.setUsername("example");
        user.setName("example");
        user.setPasswordHash("hashed_password");
        user.setEmail("example@gmail.com");
        user.setCreated(LocalDateTime.now());
        user.setLastUpdated(user.getCreated());
        user.setRoles("ROLE_ADMIN,ROLE_MANAGER,ROLE_MEMBER");

        Reagent reagent = new Reagent();
        reagent.setName("example");
        reagent.setUser(user);

        return reagent;
    }
}
