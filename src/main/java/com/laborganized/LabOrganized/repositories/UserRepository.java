package com.laborganized.LabOrganized.repositories;

import com.laborganized.LabOrganized.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
