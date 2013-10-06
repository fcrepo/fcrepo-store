package com.github.cwilper.fcrepo.store.legacy;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PreDestroy;

import org.apache.derby.jdbc.EmbeddedDataSource40;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.cwilper.fcrepo.store.core.StoreException;

public class ExistingDerbyDB extends JdbcTemplate {
    
    private final File dbDir;

    private boolean closed;

    public ExistingDerbyDB(String path) {
        try {
            dbDir = new File(path);
            setDataSource(createDataSource(true));
        } catch (SQLException e) {
            throw new StoreException("Error creating temporary db", e);
        }
    }

    // create if true, shutdown if false
    private EmbeddedDataSource40 createDataSource(boolean create)
            throws SQLException {
        EmbeddedDataSource40 dataSource = new EmbeddedDataSource40();
        dataSource.setDatabaseName(dbDir.toString());
        if (create) {
            dataSource.setCreateDatabase("create");
        } else {
            dataSource.setShutdownDatabase("shutdown");
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.warn("Error closing connection", e);
                }
            }
        }
        return dataSource;
    }

    @PreDestroy
    public void delete() {
        if (!closed) {
            try {
                createDataSource(false);
            } catch (SQLException e) {
                // SQL exception XJ015 is expected
            } finally {
                closed = true;
            }
        }
    }

}