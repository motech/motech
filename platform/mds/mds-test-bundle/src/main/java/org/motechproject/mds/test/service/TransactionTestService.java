package org.motechproject.mds.test.service;

/**
 * This service is responsible for making sure Spring transactions work in the modules.
 */
public interface TransactionTestService {

    void addTwoBooks();

    void addTwoBooksAndRollback();
}
