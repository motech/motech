package org.motechproject.mds.jdo;

import org.springframework.orm.jdo.DefaultJdoDialect;
import org.springframework.transaction.TransactionDefinition;

import javax.jdo.Constants;
import javax.jdo.Transaction;

/**
 * This is an extensions of Springs default JDO dialect that allows controlling the transaction
 * serialization level per transaction with Spring transactions. This was fixed in newer versions of Spring
 * and this class should get removed once we upgrade to a newer Spring version.
 */
public class MdsJdoDialect extends DefaultJdoDialect {

    public MdsJdoDialect(Object connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public Object beginTransaction(Transaction transaction, TransactionDefinition definition)  {
        String jdoIsolationLevel = getJdoIsolationLevel(definition);
        if (jdoIsolationLevel != null) {
            transaction.setIsolationLevel(jdoIsolationLevel);
        }
        transaction.begin();
        return null;
    }

    protected String getJdoIsolationLevel(TransactionDefinition definition) {
        switch (definition.getIsolationLevel()) {
            case TransactionDefinition.ISOLATION_SERIALIZABLE:
                return Constants.TX_SERIALIZABLE;
            case TransactionDefinition.ISOLATION_REPEATABLE_READ:
                return Constants.TX_REPEATABLE_READ;
            case TransactionDefinition.ISOLATION_READ_COMMITTED:
                return Constants.TX_READ_COMMITTED;
            case TransactionDefinition.ISOLATION_READ_UNCOMMITTED:
                return Constants.TX_READ_UNCOMMITTED;
            default:
                return null;
        }
    }
}
