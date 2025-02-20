package com.laborganized.LabOrganized.DTOs;

import com.laborganized.LabOrganized.models.Storeable;
import com.laborganized.LabOrganized.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
        private Long id;
        private String username;
        private String name;
        private String email;
        private List<Long> storeableIds;

    public UserDTO() {
    }

    public UserDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.name = user.getName();
            this.email = user.getEmail();
            this.storeableIds = user.getStoreableList().stream()
                    .map(Storeable::getId)
                    .collect(Collectors.toList());
        }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getStoreableIds() {
        return storeableIds;
    }

    public void setStoreableIds(List<Long> storeableIds) {
        this.storeableIds = storeableIds;
    }
}
