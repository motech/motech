package org.motechproject.mds.helper;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.testutil.EntitySchemaBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntitiesTopologyTest {
    protected Entity entityA;
    protected Entity entityB;
    protected Entity entityC;
    protected Entity entityD;
    protected Entity entityE;
    protected Entity entityF;

    protected <E> Set<E> asSet(E... elements) {
        Set<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    protected <E> List<E> asList(E... elements) {
        List<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    //    A -> B -> D
    //         |
    //         v
    //         C
    protected void setupSimpleTopology() {
        entityA = EntitySchemaBuilder.eude("A")
                .field("relB", "mds.field.relationship.oneToOne").relatedClass("B").done()
                .build();
        entityB = EntitySchemaBuilder.eude("B")
                .field("relC", "mds.field.relationship.oneToOne").relatedClass("C").done()
                .field("relD", "mds.field.relationship.oneToOne").relatedClass("D").done()
                .build();
        entityC = EntitySchemaBuilder.eude("C").build();
        entityD = EntitySchemaBuilder.eude("D").build();
    }

    //    A -> B -> D
    //    |    |
    //    v    v
    //    E <- C -> F
    protected void setupComplexTopology() {
        entityA = EntitySchemaBuilder.eude("A")
                .field("relB", "mds.field.relationship.oneToOne").relatedClass("B").done()
                .field("relE", "mds.field.relationship.oneToOne").relatedClass("E").done()
                .build();
        entityB = EntitySchemaBuilder.eude("B")
                .field("relC", "mds.field.relationship.oneToOne").relatedClass("C").done()
                .field("relD", "mds.field.relationship.oneToOne").relatedClass("D").done()
                .build();
        entityC = EntitySchemaBuilder.eude("C")
                .field("relE", "mds.field.relationship.oneToOne").relatedClass("E").done()
                .field("relF", "mds.field.relationship.oneToOne").relatedClass("F").done()
                .build();
        entityD = EntitySchemaBuilder.eude("D").build();
        entityE = EntitySchemaBuilder.eude("E").build();
        entityF = EntitySchemaBuilder.eude("F").build();
    }

    //    A -> B <- D
    //    ^    |
    //    |    v
    //    E <- C -> F
    protected void setupComplexTopologyWithLoop() {
        entityA = EntitySchemaBuilder.eude("A")
                .field("relB", "mds.field.relationship.oneToOne").relatedClass("B").done()
                .build();
        entityB = EntitySchemaBuilder.eude("B")
                .field("relC", "mds.field.relationship.oneToOne").relatedClass("C").done()
                .build();
        entityC = EntitySchemaBuilder.eude("C")
                .field("relE", "mds.field.relationship.oneToOne").relatedClass("E").done()
                .field("relF", "mds.field.relationship.oneToOne").relatedClass("F").done()
                .build();
        entityD = EntitySchemaBuilder.eude("D")
                .field("relB", "mds.field.relationship.oneToOne").relatedClass("B").done()
                .build();
        entityE = EntitySchemaBuilder.eude("E")
                .field("relA", "mds.field.relationship.oneToOne").relatedClass("A").done()
                .build();
        entityF = EntitySchemaBuilder.eude("F").build();
    }
}
