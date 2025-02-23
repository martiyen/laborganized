package com.laborganized.LabOrganized.repositories;

import com.laborganized.LabOrganized.models.Container;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends CrudRepository<Container, Long> {
    Container findByName(String name);
}
