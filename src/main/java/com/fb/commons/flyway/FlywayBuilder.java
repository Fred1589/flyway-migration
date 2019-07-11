package com.fb.commons.flyway;

import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;

public class FlywayBuilder {

    private DataSource dataSource;
    private StageProperties properties;
    private Map<String, String> placeHolders;

    /**
     * Set environment properties
     * 
     * @param properties
     * @return
     */
    public FlywayBuilder withEnviromentProperties(final StageProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Set datasource
     * 
     * @param dataSource
     * @return
     */
    public FlywayBuilder withDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     * Set placeholders
     * 
     * @param placeHolders
     * @return
     */
    public FlywayBuilder withPlaceHolders(final Map<String, String> placeHolders) {
        this.placeHolders = placeHolders;
        return this;
    }

    /**
     * Build Flyway instance
     * 
     * @return
     */
    public Flyway build() {
        final String locations = properties.getLocations();
        final String versionTable = properties.getVersionTable();
        final String initialVersion = properties.getInitialVersion();

        Flyway flyway = new Flyway();
        flyway.setLocations(locations.split(","));
        flyway.setTable(versionTable);
        flyway.setBaselineVersion(MigrationVersion.fromVersion(initialVersion));
        flyway.setValidateOnMigrate(true);
        flyway.setDataSource(dataSource);
        flyway.setPlaceholders(placeHolders);

        return flyway;
    }

}
