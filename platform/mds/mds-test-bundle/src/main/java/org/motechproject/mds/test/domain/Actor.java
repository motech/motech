package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.IndexedManyToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(recordHistory = true)
public class Actor {

    private Long id;

    @Field(required = true)
    private String name;

    @Field
    @IndexedManyToMany(relatedField = "actors")
    private List<Movie> movies;

    public Actor() {
        this(null);
    }

    public Actor(String title) {
        this.name = title;
        this.movies = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Movie> getMovies() {
        if (movies == null) {
            movies = new ArrayList<>();
        }
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Actor other = (Actor) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

