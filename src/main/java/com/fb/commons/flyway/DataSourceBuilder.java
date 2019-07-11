package com.fb.commons.flyway;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.fb.commons.crypt.Crypt;

public final class DataSourceBuilder {

    private DataSourceBuilder() {
        // empty constructor
    }

    /**
     * Create data source to given properties. Actually there are two database types
     * supported (Oracle, PostgreSQL).
     *
     * @param properties   - database connection parameters url, user, password
     * @param databaseType - Database to connect to
     * @param privateKey   - to decrypt the password to connect to database
     * @return
     */
    public static DataSource buildSchemaDatasource(final StageProperties properties, final DatabaseType databaseType,
            final byte[] privateKey) {
        try {
            DataSource dataSource = null;
            switch (databaseType) {
            case POSTGRESQL:
                dataSource = (DataSource) Class.forName("org.postgresql.ds.PGSimpleDataSource").newInstance();
                ((org.postgresql.ds.PGSimpleDataSource) dataSource).setUser(properties.getUserName());
                System.out.println("Decrypting password");
                ((org.postgresql.ds.PGSimpleDataSource) dataSource)
                        .setPassword(Crypt.decrypt(privateKey, properties.getPassword()));
                ((org.postgresql.ds.PGSimpleDataSource) dataSource).setUrl(properties.getUrl());
                break;
            default:
                throw new IllegalArgumentException("Unknown database type: " + databaseType);
            }

            checkDataSource(dataSource, databaseType);

            return dataSource;
        } catch (final Exception e) {
            throw new MigrationException("Could not create datasource for flyway", e);
        }
    }

    private static void checkDataSource(DataSource ds, DatabaseType databaseType) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            switch (databaseType) {
            case ORACLE:
                stmt.execute("SELECT 1 FROM DUAL");
                break;
            case POSTGRESQL:
                stmt.execute("SELECT 1");
                break;
            default:
                throw new IllegalArgumentException("Unknown database type: " + databaseType);
            }
        } catch (SQLException e) {
            throw new MigrationException("Could not validate datasource", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
