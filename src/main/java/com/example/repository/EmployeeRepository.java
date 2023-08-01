package com.example.repository;

import com.example.models.Employee;
import com.example.models.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.EnumSet;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<Employee> {

    public Employee findByName(String name) {
        return find("name",name).firstResult();
    }

    public boolean existsByName(String name){
        return find("name",name).firstResult() !=null;
    }
    public boolean isValidRole(Role role){
        return EnumSet.allOf(Role.class).contains(role);
    }

}
