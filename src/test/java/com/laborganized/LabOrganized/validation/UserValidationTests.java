package com.laborganized.LabOrganized.validation;

import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.models.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class UserValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWhenValidUser() {
        User user = getValidUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void shouldFailValidationWhenUsernameIsEmpty() {
        User user = getValidUser();
        user.setUsername(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Username must not be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aa", "UsernameLongerThanThirtyCharacters"})
    void shouldFailValidationWhenUsernameIsShorterThan3OrLongerThan30(String input) {
        User user = getValidUser();
        user.setUsername(input);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Username must be between 3 and 30 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ex.ample", "ex'ample", "ex-ample", "ex_ample", "ex ample", "ex/ample", "ex\\ample"})
    void shouldFailValidationWhenUsernamePatternNotValid(String input) {
        User user = getValidUser();
        user.setUsername(input);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Username can only contain alphanumeric values");
    }

    @Test
    void shouldFailValidationWhenNameIsEmpty() {
        User user = getValidUser();
        user.setName(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must not be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "NameLongerThanFiftyCharactersAAAAAAAAAAAAAAAAAAAAAA"})
    void shouldFailValidationWhenNameIsShorterThan2OrLongerThan50Characters(String input) {
        User user = getValidUser();
        user.setName(input);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.forEach(System.out::println);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must be between 3 and 50 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {".example", "example.", "ex..ample", "'example", "example'", "ex''ample", "-example", "example-", "ex--ample", " example", "example ", "ex  ample"})
    void shouldFailValidationWhenNamePatternIsNotValid(String input) {
        User user = getValidUser();
        user.setName(input);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.forEach(System.out::println);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Name can only contain letters, whitespaces, apostrophes and dashes");
    }

    @Test
    void shouldFailValidationWhenPasswordIsEmpty() {
        User user = getValidUser();
        user.setPasswordHash(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must not be empty");
    }

    @Test
    void shouldFailValidationWhenUserRoleIsEmpty() {
        User user = getValidUser();
        user.setUserRole(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("User role must be defined");
    }

    @ParameterizedTest
    @ValueSource(strings = {"NotAnEmail", "  ", ""})
    void shouldFailValidationWhenEmailNotValid(String input) {
        User user = getValidUser();
        user.setEmail(input);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Please enter a valid email");
    }

    @Test
    void shouldFailValidationWhenCreatedIsEmpty() {
        User user = getValidUser();
        user.setCreated(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Date of creation must not be null");
    }

    @Test
    void shouldFailValidationWhenLastUpdatedIsEmpty() {
        User user = getValidUser();
        user.setLastUpdated(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        AssertionsForClassTypes.assertThat(violations.size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage()).isEqualTo("Date of last update must not be null");
    }

    private User getValidUser() {
        User user = new User();
        user.setUsername("example");
        user.setName("example");
        user.setPasswordHash("hashed_password");
        user.setEmail("example@gmail.com");
        user.setCreated(LocalDateTime.now());
        user.setLastUpdated(user.getCreated());
        user.setUserRole(UserRole.ADMIN);

        return user;
    }


}
