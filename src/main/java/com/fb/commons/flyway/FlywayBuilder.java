package com.fb.commons.flyway;

import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;

public class FlywayBuilder {

	private DataSource dataSource;
	private StageProperties properties;
	private Map<String, String> placeHolders;
	private String schema;

	/**
	 * Set environment properties
	 * 
	 * @param properties
	 * @return FlywayBuilder
	 */
	public FlywayBuilder withEnviromentProperties(final StageProperties properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Set datasource
	 * 
	 * @param dataSource
	 * @return FlywayBuilder
	 */
	public FlywayBuilder withDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	/**
	 * Set placeholders
	 * 
	 * @param placeHolders
	 * @return FlywayBuilder
	 */
	public FlywayBuilder withPlaceHolders(final Map<String, String> placeHolders) {
		this.placeHolders = placeHolders;
		return this;
	}

	/**
	 * Set schema
	 * 
	 * @param schema
	 * @return FlywayBuilder
	 */
	public FlywayBuilder withSchema(final String schema) {
		this.schema = schema;
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

		return Flyway.configure().locations(locations.split(",")).table(versionTable)
				.baselineVersion(MigrationVersion.fromVersion(initialVersion)).validateOnMigrate(true)
				.dataSource(dataSource).placeholders(placeHolders).schemas(schema).load();
	}

}
