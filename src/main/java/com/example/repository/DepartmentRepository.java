package com.example.repository;

import com.example.models.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DepartmentRepository implements PanacheRepository<Department> {
    public Department findByDepartment(String name) {
        return find("department",name).firstResult();
    }

    public boolean hasEmployees(Long departmentId) {
        return count("id = ?1 and employees is not empty", departmentId) > 0;
    }
}
