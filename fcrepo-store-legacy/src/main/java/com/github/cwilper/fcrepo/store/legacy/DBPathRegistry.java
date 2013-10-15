package com.github.cwilper.fcrepo.store.legacy;

import com.github.cwilper.fcrepo.store.core.StoreException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Database-backed {@link PathRegistry} implementation.
 */
public class DBPathRegistry implements PathRegistry {
    private static final String ARG_REGEX = "\\?";
    private static final String CREATE_TABLE_DDL =
            "CREATE TABLE ? (\n"
            + "id VARCHAR(256) PRIMARY KEY NOT NULL,\n"
            + "path VARCHAR(4096) NOT NULL)";
    private static final String SELECT_COUNT_SQL =
            "SELECT COUNT(*) FROM ?";
    private static final String SELECT_PATH_SQL =
            "SELECT PATH FROM ? WHERE ID = ?";
    private static final String INSERT_PATH_SQL =
            "INSERT INTO ? (id, path) VALUES (?, ?)";
    private static final String UPDATE_PATH_SQL =
            "UPDATE ? SET path = ? WHERE id = ?";
    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM ? WHERE id = ?";
    private static final String DELETE_ALL_SQL =
            "DELETE FROM ?";
    
    private final String select_count_sql;
    private final String select_path_sql;
    private final String insert_path_sql;
    private final String update_path_sql;
    private final String delete_by_id_sql;
    private final String delete_all_sql;

    private final JdbcTemplate db;
    private final String table;

    /**
     * Creates an instance.
     *
     * @param db the database to use.
     * @param table the name of the table, which will be created if it doesn't
     *              exist.
     * @throws NullPointerException if either argument is null.
     */
    public DBPathRegistry(JdbcTemplate db, String table) {
        if (db == null || table == null) throw new NullPointerException();
        this.db = db;
        this.table = table;
        select_count_sql = SELECT_COUNT_SQL.replace("?", table);
        select_path_sql = SELECT_PATH_SQL.replaceFirst(ARG_REGEX, table);
        insert_path_sql = INSERT_PATH_SQL.replaceFirst(ARG_REGEX, table);
        update_path_sql = UPDATE_PATH_SQL.replaceFirst(ARG_REGEX, table);
        delete_by_id_sql = DELETE_BY_ID_SQL.replaceFirst(ARG_REGEX, table);
        delete_all_sql = DELETE_ALL_SQL.replace("?", table);
        createTableIfNeeded();
    }

    private void createTableIfNeeded() {
        try {
            getPathCount();
        } catch (StoreException e) {
            try {
                db.execute(CREATE_TABLE_DDL.replace("?", table));
                getPathCount();
            } catch (Exception e2) {
                throw new StoreException("Error creating table", e2);
            }
        }
    }

    /**
     * Deletes all rows from the table, for testing.
     */
    public void clear() {
        try {
            db.update(delete_all_sql);
        } catch (DataAccessException e) {
            throw new StoreException("Error clearing table", e);
        }
    }

    @Override
    public long getPathCount() {
        try {
            return db.queryForLong(select_count_sql);
        } catch (DataAccessException e) {
            throw new StoreException("Error getting path count", e);
        }
    }

    @Override
    public String getPath(String id) {
        try {
            List<String> result = db.queryForList(
                    select_path_sql,
                    new String[] { id }, String.class);
            if (result.size() == 0) return null;
            return result.get(0);
        } catch (DataAccessException e) {
            throw new StoreException("Error getting path", e);
        }
    }

    @Override
    public void setPath(String id, String path, PathAlgorithm alg) {
        String existing = getPath(id);
        boolean exists = existing != null;
        try {
            if (!exists && path != null) {
                db.update(insert_path_sql,
                        id, path);
            } else if (exists) {
                if (path != null) {
                    GregorianCalendar oc = alg.dateOf(existing);
                    GregorianCalendar nc = alg.dateOf(path);
                    if (nc != null && oc != null && nc.compareTo(oc) > 0) {
                        db.update(update_path_sql,
                            path, id);
                    }
                } else {
                    db.update(delete_by_id_sql,
                            id);
                }
            }
        } catch (DataAccessException e) {
            throw new StoreException("Error setting path", e);
        }
    }
}
