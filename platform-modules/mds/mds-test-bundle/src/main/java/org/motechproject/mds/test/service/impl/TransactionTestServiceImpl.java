package org.motechproject.mds.test.service.impl;

import org.motechproject.mds.test.domain.manytomany.Book;
import org.motechproject.mds.test.service.manytomany.BookDataService;
import org.motechproject.mds.test.service.TransactionTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for making sure Spring transactions work in the modules.
 */
@Component("transactionTestService")
public class TransactionTestServiceImpl implements TransactionTestService {

    @Autowired
    private BookDataService bookDataService;

    @Transactional
    public void addTwoBooks() {
        bookDataService.create(new Book("txBook1"));
        bookDataService.create(new Book("txBook2"));
    }

    @Transactional
    public void addTwoBooksAndRollback() {
        addTwoBooks();
        throw new IllegalStateException("Rollback the transaction");
    }
}
