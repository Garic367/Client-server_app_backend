package com.example.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "Employee")
public class Employee extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long id;
    @Column(name = "name", nullable = false, unique = true, length = 30)
    public String name;

    public String getName() {
        return name;
    }

    @Min(100)
    @Max(1000)
    @Column(name = "salary",nullable = false)
    public Integer salary;

    @ManyToOne
    @JoinColumn(name = "department_id")
    public Department department;


    public Department getDepartment() {
        return department;
    }
    public long getId() {
        return id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    public Role role;
    @Column(name = "onVacation",nullable = false )
    public Boolean onVacation;

}

