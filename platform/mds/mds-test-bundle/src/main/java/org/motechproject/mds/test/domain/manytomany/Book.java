package org.motechproject.mds.test.domain.manytomany;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.MultiRelationshipDisplay;

import javax.jdo.annotations.Persistent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Book {

    private Long id;

    @Field(required = true)
    private String title;

    @Field
    @Persistent(mappedBy = "books")
    @MultiRelationshipDisplay(expandByDefault=false, showCount = false, allowAddingNew = true, allowAddingExisting = false)
    private Set<Author> authors;

    public Book() {
        this(null);
    }

    public Book(String title) {
        this.title = title;
        this.authors = new HashSet<Author>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Author> getAuthors() {
        if (authors == null) {
            authors = new HashSet<Author>();
        }
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public void addAuthors(Set<Author> authors) {
        this.getAuthors().addAll(authors);
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

        final Book other = (Book) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.title, other.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

