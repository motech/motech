package org.motechproject.mds.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.ManyToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Tracking;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.entity.InvalidEntitySettingsException;
import org.motechproject.mds.ex.entity.InvalidRelationshipException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;

public class EntitySorterTest {

    private Entity childEntity;
    private Entity parentEntity;
    private Entity entity1;
    private Entity entity2;
    private Entity entity3;
    private Entity book;
    private Entity author;

    private Entity binaryTree;

    @Test
    public void shouldProperlySortByHasARelation() {
        List<Entity> entities = EntitySorter.sortByHasARelation(getValidDataModel());

        Assert.assertTrue(entities.indexOf(entity3) < entities.indexOf(entity2));
        Assert.assertTrue(entities.indexOf(entity3) < entities.indexOf(entity1));

        Assert.assertTrue(entities.indexOf(entity2) < entities.indexOf(entity1));
        Assert.assertTrue(entities.indexOf(entity2) > entities.indexOf(entity3));
    }

    @Test
    public void shouldProperlySortByInheritance() {
        List<Entity> initialList = new ArrayList<>();
        initialList.add(childEntity);
        initialList.add(parentEntity);

        List<Entity> entities = EntitySorter.sortByInheritance(initialList);

        Assert.assertTrue(entities.indexOf(parentEntity) < entities.indexOf(childEntity));
    }

    @Test
    public void shouldNotSignalErrorOnSelfRelation() {
        List<Entity> initialList = new ArrayList<>();
        initialList.addAll(getValidDataModel());
        initialList.add(binaryTree);

        List<Entity> sortingResult = EntitySorter.sortByHasARelation(initialList);

        Assert.assertEquals(sortingResult.size(), 6);
        Assert.assertTrue(sortingResult.contains(binaryTree));
    }

    @Test(expected = InvalidRelationshipException.class)
    public void shouldSignalInvalidBidirectionalRelationship() {
        EntitySorter.sortByHasARelation(getInvalidDataModel());
    }

    @Test(expected = InvalidEntitySettingsException.class)
    public void shouldSignalInvalidHistoryTrackingSettings() {
        List<Entity> entities = getValidDataModel();

        Tracking noHistory = new Tracking(entities.get(0));
        noHistory.setRecordHistory(false);
        entities.get(0).setTracking(noHistory);

        EntitySorter.sortByHasARelation(entities);
    }

    private List<Entity> getInvalidDataModel() {
        return Arrays.asList(book, author, entity1, entity2, entity3);
    }

    private List<Entity> getValidDataModel() {
        //Get invalid data model and fix it, by adding necessary metadata to bi-directional relationship
        List<Entity> entities = getInvalidDataModel();

        Field authorField = entities.get(0).getField("author");
        authorField.addMetadata(new FieldMetadata(authorField, RELATED_FIELD, "book"));

        return entities;
    }

    @Before
    public void setUpEntities() {
        book = new Entity("Book");
        author = new Entity("Author");

        Field bookField = new Field(book, "author", "author", new Type(ManyToManyRelationship.class));
        bookField.addMetadata(new FieldMetadata(bookField, RELATED_CLASS, "Author"));

        Field authorField = new Field(author, "book", "book", new Type(ManyToManyRelationship.class));
        authorField.addMetadata(new FieldMetadata(authorField, RELATED_CLASS, "Book"));

        book.addField(bookField);
        author.addField(authorField);

        entity1 = new Entity("Entity1");
        entity2 = new Entity("Entity2");
        entity3 = new Entity("Entity3");

        Field entity1Field = new Field(entity1, "entity1", "entity1", new Type(OneToOneRelationship.class));
        entity1Field.addMetadata(new FieldMetadata(entity1Field, RELATED_CLASS, "Entity2"));

        Field entity2Field = new Field(entity2, "entity2", "entity2", new Type(OneToOneRelationship.class));
        entity2Field.addMetadata(new FieldMetadata(entity2Field, RELATED_CLASS, "Entity3"));

        entity1.addField(entity1Field);
        entity2.addField(entity2Field);

        parentEntity = new Entity("Parent");
        childEntity = new Entity("Child");
        childEntity.setSuperClass("Parent");

        binaryTree = new Entity("Binary tree");
        Field leftChild = new Field(binaryTree, "leftChild", "Left child", new Type(OneToOneRelationship.class));
        leftChild.addMetadata(new FieldMetadata(leftChild, RELATED_CLASS, "Binary tree"));

        Field rightChild = new Field(binaryTree, "rightChild", "Right child", new Type(OneToOneRelationship.class));
        rightChild.addMetadata(new FieldMetadata(rightChild, RELATED_CLASS, "Binary tree"));

        binaryTree.addField(leftChild);
        binaryTree.addField(rightChild);

        setHistoryTracking(entity1);
        setHistoryTracking(entity2);
        setHistoryTracking(entity3);
        setHistoryTracking(book);
        setHistoryTracking(author);
        setHistoryTracking(parentEntity);
        setHistoryTracking(childEntity);
        setHistoryTracking(binaryTree);
    }

    private void setHistoryTracking(Entity entity) {
        Tracking tracking = new Tracking(entity);
        tracking.setRecordHistory(true);
        entity.setTracking(tracking);
    }
}
