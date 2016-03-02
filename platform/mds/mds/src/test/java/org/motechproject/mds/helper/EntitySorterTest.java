package org.motechproject.mds.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.ManyToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.exception.entity.InvalidEntitySettingsException;
import org.motechproject.mds.exception.entity.InvalidRelationshipException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;

@RunWith(MockitoJUnitRunner.class)
public class EntitySorterTest {

    private EntityDto childEntity;
    private EntityDto parentEntity;
    private EntityDto entity1;
    private EntityDto entity2;
    private EntityDto entity3;
    private EntityDto book;
    private EntityDto author;

    private EntityDto binaryTree;

    private FieldDto authorFieldInBook;

    @Mock
    private SchemaHolder schemaHolder;

    @Test
    public void shouldProperlySortByHasARelation() {
        fixDataModel();
        List<EntityDto> entities = EntitySorter.sortByHasARelation(getDataModel(), schemaHolder);

        Assert.assertTrue(entities.indexOf(entity3) < entities.indexOf(entity2));
        Assert.assertTrue(entities.indexOf(entity3) < entities.indexOf(entity1));

        Assert.assertTrue(entities.indexOf(entity2) < entities.indexOf(entity1));
        Assert.assertTrue(entities.indexOf(entity2) > entities.indexOf(entity3));
    }

    @Test
    public void shouldProperlySortByInheritance() {
        List<EntityDto> initialList = new ArrayList<>();
        initialList.add(childEntity);
        initialList.add(parentEntity);

        List<EntityDto> entities = EntitySorter.sortByInheritance(initialList);

        Assert.assertTrue(entities.indexOf(parentEntity) < entities.indexOf(childEntity));
    }

    @Test
    public void shouldNotSignalErrorOnSelfRelation() {
        fixDataModel();
        List<EntityDto> initialList = new ArrayList<>();
        initialList.addAll(getDataModel());
        initialList.add(binaryTree);

        List<EntityDto> sortingResult = EntitySorter.sortByHasARelation(initialList, schemaHolder);

        Assert.assertEquals(sortingResult.size(), 6);
        Assert.assertTrue(sortingResult.contains(binaryTree));
    }

    @Test(expected = InvalidRelationshipException.class)
    public void shouldSignalInvalidBidirectionalRelationship() {
        EntitySorter.sortByHasARelation(getDataModel(), schemaHolder);
    }

    @Test(expected = InvalidEntitySettingsException.class)
    public void shouldSignalInvalidHistoryTrackingSettings() {
        fixDataModel();
        List<EntityDto> entities = getDataModel();
        entities.get(0).setRecordHistory(false);

        EntitySorter.sortByHasARelation(entities, schemaHolder);
    }

    private List<EntityDto> getDataModel() {
        return asList(book, author, entity1, entity2, entity3);
    }

    private void fixDataModel() {
        authorFieldInBook.addMetadata(new MetadataDto(RELATED_FIELD, "book"));
    }

    @Before
    public void setUpEntities() {
        book = new EntityDto("Book");
        author = new EntityDto("Author");

        authorFieldInBook = fieldDto("author", ManyToManyRelationship.class);
        authorFieldInBook.addMetadata(new MetadataDto(RELATED_CLASS, "Author"));

        FieldDto bookFieldInAuthor = fieldDto("book", ManyToManyRelationship.class);
        bookFieldInAuthor.addMetadata(new MetadataDto(RELATED_CLASS, "Book"));

        when(schemaHolder.getFields(book)).thenReturn(singletonList(authorFieldInBook));
        when(schemaHolder.getFields(author)).thenReturn(singletonList(bookFieldInAuthor));

        entity1 = new EntityDto("Entity1");
        entity2 = new EntityDto("Entity2");
        entity3 = new EntityDto("Entity3");

        FieldDto entity1Field = fieldDto("entity1", OneToOneRelationship.class);
        entity1Field.addMetadata(new MetadataDto(RELATED_CLASS, "Entity2"));

        FieldDto entity2Field = fieldDto("entity2", OneToOneRelationship.class);
        entity2Field.addMetadata(new MetadataDto(RELATED_CLASS, "Entity3"));

        when(schemaHolder.getFields(entity1)).thenReturn(singletonList(entity1Field));
        when(schemaHolder.getFields(entity2)).thenReturn(singletonList(entity2Field));

        parentEntity = new EntityDto("Parent");
        childEntity = new EntityDto("Child");
        childEntity.setSuperClass("Parent");

        binaryTree = new EntityDto("Binary tree");
        FieldDto leftChild = fieldDto("leftChild", "Left child", OneToOneRelationship.class);
        leftChild.addMetadata(new MetadataDto(RELATED_CLASS, "Binary tree"));

        FieldDto rightChild = fieldDto("rightChild", "Right child", OneToOneRelationship.class);
        rightChild.addMetadata(new MetadataDto(RELATED_CLASS, "Binary tree"));

        when(schemaHolder.getFields(binaryTree)).thenReturn(asList(leftChild, rightChild));

        entity1.setRecordHistory(true);
        entity2.setRecordHistory(true);
        entity3.setRecordHistory(true);
        book.setRecordHistory(true);
        author.setRecordHistory(true);
        parentEntity.setRecordHistory(true);
        childEntity.setRecordHistory(true);
        binaryTree.setRecordHistory(true);
    }
}
