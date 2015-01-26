package org.motechproject.mds.jdo;

import org.datanucleus.store.rdbms.adapter.MySQLAdapter;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.TableImpl;

import java.sql.DatabaseMetaData;
import java.util.Properties;

/**
 * Adapter for MySQL that extends default Datanucleus MySQL adapter and
 * adds ROW_FORMAT to table creation.
 */
public class MySQLDataStoreAdapter extends MySQLAdapter {

    public MySQLDataStoreAdapter(DatabaseMetaData metadata) {
        super(metadata);
    }

    @Override
    public String getCreateTableStatement(TableImpl table, Column[] columns, Properties props, IdentifierFactory factory) {
        StringBuilder sb = new StringBuilder(super.getCreateTableStatement(table, columns, props, factory));
        sb.append(" ROW_FORMAT=DYNAMIC");
        return sb.toString();
    }
}
