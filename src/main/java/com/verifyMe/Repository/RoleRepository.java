package com.verifyMe.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.verifyMe.Entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
