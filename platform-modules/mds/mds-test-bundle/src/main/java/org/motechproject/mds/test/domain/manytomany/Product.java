package org.motechproject.mds.test.domain.manytomany;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.IndexedManyToMany;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "products", recordHistory = true)
public class Product {

    private Long id;

    @Field(required = true)
    private String productName;

    @Field
    @Join(column = "product_id")
    @Element(column = "supplier_id")
    @Persistent(table = "products_join_suppliers", defaultFetchGroup = "true")
    @IndexedManyToMany(relatedField = "products")
    private List<Supplier> suppliers;

    public Product() {
        this(null);
    }

    public Product(String title) {
        this.productName = title;
        this.suppliers = new ArrayList<>();
    }

    public String getName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<Supplier> getSuppliers() {
        if (suppliers == null) {
            suppliers = new ArrayList<>();
        }
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
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

        final Product other = (Product) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.productName, other.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
