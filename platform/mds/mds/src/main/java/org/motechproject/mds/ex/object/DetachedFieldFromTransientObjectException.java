package org.motechproject.mds.ex.object;

import org.motechproject.mds.ex.MdsException;

/**
 * Exception, that signalizes getting detached field from object in transient state
 */
public class DetachedFieldFromTransientObjectException extends MdsException {
    private static final long serialVersionUID = 5232196256542540610L;

    public DetachedFieldFromTransientObjectException() {
        super("When you want to get detached field from object, you have to retrieve the object from database in the same transaction," +
                "for example : " +
                "Object blobValue = dataService.doInTransaction(new TransactionCallback() {\n" +
                "   @Override\n" +
                "   public Object doInTransaction(TransactionStatus transactionStatus) {\n" +
                "       Animal animal = dataService.findById(1L);\n" +
                "       return dataService.getDetachedField(animal, \"someBlob\");" +
                "   }\n" +
                "});" +
                "you cannot retrieve the object outside the transaction. Also you can use overloaded method like the following :" +
                "Object blobValue = dataService.getDetachedField(animal.getId(), \"someBlob\")");
    }
}
