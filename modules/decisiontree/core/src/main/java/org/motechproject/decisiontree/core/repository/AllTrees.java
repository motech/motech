package org.motechproject.decisiontree.core.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.decisiontree.core.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllTrees extends MotechBaseRepository<Tree> {
    @Autowired
    public AllTrees(@Qualifier("treesDatabase") CouchDbConnector db) {
        super(Tree.class, db);
    }

    @GenerateView
    public Tree findByName(String name) {
        return singleResult(queryView("by_name", name));
    }

    public void addOrReplace(Tree entity) {
        super.addOrReplace(entity, "name", entity.getName());
    }
}
