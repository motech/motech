package org.motechproject.mds.ex.object;

import org.motechproject.mds.ex.MdsException;

/**
 * Exception, that signalizes updating object from transient state
 */
public class UpdateFromTransientException extends MdsException {

    private static final long serialVersionUID = 3815424623777042327L;

    public UpdateFromTransientException() {
        super("When you want to update object you have to retrieve the object from database in the same transaction," +
                "for example : " +
                "dataService.doInTransaction(new TransactionCallbackWithoutResult() {\n" +
                "            @Override\n" +
                "            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {\n" +
                "                Animal animalToUpdate = dataService.findById(1L);\n" +
                "                animalToUpdate.setYears(5);\n" +
                "                dataService.update(animalToUpdate);\n" +
                "            }\n" +
                "        });" +
                "you cannot retrieve the object you want to update outside the transaction");
    }
}
