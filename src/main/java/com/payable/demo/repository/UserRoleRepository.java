package com.payable.demo.repository;

import com.payable.demo.model.UserRole;
import com.payable.demo.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
