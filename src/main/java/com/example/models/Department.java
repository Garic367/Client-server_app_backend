package com.example.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "department")
public class Department extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long id;

    @Column(name = "name", unique = true, nullable = false)
    public String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @JsonbTransient
    public List<Employee> employees;


    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}


