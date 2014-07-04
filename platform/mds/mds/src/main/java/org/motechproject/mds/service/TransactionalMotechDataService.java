package org.motechproject.mds.service;

import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.List;

/**
 * The main goal of the <code>TransactionalMotechDataService</code> class is to resolve problems
 * with transaction annotations not working for generated lookups. We use the traditional transaction callback instead.
 *
 * @param <T> the type of entity schema.
 */
public abstract class TransactionalMotechDataService<T> extends DefaultMotechDataService<T> {

    @Override
    protected long count(final List<Property> properties) {
        return doInTransaction(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                InstanceSecurityRestriction securityRestriction = validateCredentials();
                return getRepository().count(properties, securityRestriction);
            }
        });
    }

    @Override
    protected List<T> retrieveAll(final List<Property> properties) {
        return doInTransaction(new TransactionCallback<List<T>>() {
            @Override
            public List<T> doInTransaction(TransactionStatus status) {
                InstanceSecurityRestriction securityRestriction = validateCredentials();
                return getRepository().retrieveAll(properties, securityRestriction);
            }
        });
    }

    @Override
    protected List<T> retrieveAll(final List<Property> properties, final QueryParams queryParams) {
        return doInTransaction(new TransactionCallback<List<T>>() {
            @Override
            public List<T> doInTransaction(TransactionStatus status) {
                InstanceSecurityRestriction securityRestriction = validateCredentials();
                return getRepository().retrieveAll(properties, queryParams, securityRestriction);
            }
        });
    }

}
