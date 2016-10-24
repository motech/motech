package org.motechproject.tasks.dto;

import java.util.List;

public class TaskDataProviderDto {

    private Long id;
    private String name;
    private List<TaskDataProviderObjectDto> objects;

    public TaskDataProviderDto(Long id, String name, List<TaskDataProviderObjectDto> objects) {
        this.id = id;
        this.name = name;
        this.objects = objects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskDataProviderObjectDto> getObjects() {
        return objects;
    }

    public void setObjects(List<TaskDataProviderObjectDto> objects) {
        this.objects = objects;
    }
}
