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

@Entity(tableName = "suppliers", recordHistory = true)
public class Supplier {

    private Long id;

    @Field(required = true)
    private String supplierName;

    @Field
    @Join(column = "supplier_id")
    @Element(column = "product_id")
    @Persistent(table = "suppliers_join_products", defaultFetchGroup = "true")
    @IndexedManyToMany(relatedField = "suppliers")
    private List<Product> products;

    public Supplier() {
        this(null);
    }

    public Supplier(String supplierName) {
        this.supplierName = supplierName;
        this.products = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Supplier other = (Supplier) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.supplierName, other.supplierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
