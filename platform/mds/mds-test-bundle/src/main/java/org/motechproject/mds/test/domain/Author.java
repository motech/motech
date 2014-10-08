package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Author {

    private Long id;

    @Field
    private String name;

    @Field
    private Set<Book> books;

    public Author(String name) {
        this.name = name;
        this.books = new HashSet<Book>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        if (books == null) {
            books = new HashSet<Book>();
        }
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addBooks(Set<Book> books) {
        this.getBooks().addAll(books);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Author other = (Author) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
