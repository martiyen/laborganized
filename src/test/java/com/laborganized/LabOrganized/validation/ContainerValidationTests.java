package com.laborganized.LabOrganized.validation;

import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.models.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ContainerValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWhenValid() {
        Container container = getValidContainer();

        Set<ConstraintViolation<Container>> violations = validator.validate(container);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void shouldNotValidateWhenNameIsEmpty() {
        Container container = getValidContainer();
        container.setName(null);

        Set<ConstraintViolation<Container>> violations = validator.validate(container);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must not be empty");
    }

    @Test
    void shouldNotValidateWhenUserIsNull() {
        Container container = getValidContainer();
        container.setUser(null);

        Set<ConstraintViolation<Container>> violations = validator.validate(container);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User must not be null");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void shouldNotValidateWhenCapacityIsNegativeOrZero(int input) {
        Container container = getValidContainer();
        container.setCapacity(input);

        Set<ConstraintViolation<Container>> violations = validator.validate(container);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Capacity must be a positive number");
    }

    private Container getValidContainer() {
        User user = new User();
        user.setUsername("example");
        user.setName("example");
        user.setPasswordHash("hashed_password");
        user.setEmail("example@gmail.com");
        user.setCreated(LocalDateTime.now());
        user.setLastUpdated(user.getCreated());
        user.setUserRole(UserRole.ADMIN);

        Container container = new Container();
        container.setName("Example");
        container.setUser(user);

        return container;
    }
}
