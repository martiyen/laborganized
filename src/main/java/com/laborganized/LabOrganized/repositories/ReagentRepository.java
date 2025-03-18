package com.laborganized.LabOrganized.repositories;

import com.laborganized.LabOrganized.models.Reagent;
import com.laborganized.LabOrganized.models.User;
import org.springframework.data.repository.CrudRepository;

public interface ReagentRepository extends CrudRepository<Reagent, Long> {

    Reagent findByName(String name);
    Iterable<Reagent> findAllByUser(User user);
}
