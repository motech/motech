package org.motechproject.mds.domain;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@javax.persistence.Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "module"})})
public class Entity {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String module;

    public Entity(String name, String module) {
        this.name = name;
        this.module = module;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }
}
