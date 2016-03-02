package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.IndexedManyToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(recordHistory = true)
public class Movie {

    private Long id;

    @Field
    private String name;

    @Field
    private Character rating;
    
    @Field
    @IndexedManyToMany(relatedField = "movies")
    private List<Actor> actors;

    public Movie(String name) {
        this.name = name;
        this.actors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getRating() {
        return rating;
    }
    
    public void setRating(Character rating) {
        this.rating = rating;
    }
    
    public List<Actor> getActors() {
        if (actors == null) {
            actors = new ArrayList<>();
        }
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
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

        final Movie other = (Movie) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
