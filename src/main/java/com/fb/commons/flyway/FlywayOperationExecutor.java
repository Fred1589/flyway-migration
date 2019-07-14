package com.fb.commons.flyway;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationVersion;

public final class FlywayOperationExecutor {
	// Field separator constant
	private static final String FIELD_SEPARATOR = " | ";
	private static final Logger LOG = Logger.getLogger(FlywayOperationExecutor.class.getName());

	public void processOperation(final FlywayOperation operation, final Flyway flyway) {

		if (FlywayOperation.CLEAN == operation) {
			LOG.info("Start database clean operation");
			System.out.print("Are you going to delete all database object?(Y/N): ");
			cleanAfterConfirmation(flyway);
			return;
		} else if (FlywayOperation.MIGRATE == operation) {
			LOG.info("Start migration");
			flyway.migrate();
			return;
		} else if (FlywayOperation.BASELINE == operation) {
			LOG.info("Start baselining database");
			System.out.print("Please insert baseline version (without V prefix, X_X_X_XX): ");
			baselineMigration(flyway);
			return;
		}

		// All other operations need an initialized schema
		if (!checkFlywayInitialized(flyway)) {
			throw new MigrationException("Schema not initialized, please initialize it first!");
		}

		if (FlywayOperation.INFO == operation) {
			dumpMigrations(flyway.info());
		} else if (FlywayOperation.VALIDATE == operation) {
			LOG.info("Start validating migration");
			try {
				flyway.validate();
			} catch (FlywayException e) {
				LOG.log(Level.INFO, "Validation failed, cause", e);
			}
		} else if (FlywayOperation.REPAIR == operation) {
			LOG.info("Start repairing version table in database");
			flyway.repair();
		} else {
			throw new MigrationException("Unknown operation: " + operation);
		}
	}

	/**
	 * Ask user to confirm cleanup operation and clean.
	 * 
	 * @param flyway
	 */
	private void cleanAfterConfirmation(final Flyway flyway) {
		try (Scanner in = new Scanner(System.in)) {
			if ("Y".equalsIgnoreCase(in.nextLine())) {
				LOG.info("Clean operation was acknowledged");
				flyway.clean();
			} else {
				LOG.info("Clean operation was canceled");
			}
		}
	}

	/**
	 * Ask user for baseline version.
	 * 
	 * @param flyway
	 */
	private void baselineMigration(final Flyway flyway) {
		Scanner in = null;
		try {
			in = new Scanner(System.in);
			String baselineVersion = in.nextLine();

			if (StringUtils.isNotBlank(baselineVersion)) {
				flyway.setBaselineVersion(MigrationVersion.fromVersion(baselineVersion));
				flyway.baseline();
				LOG.info(String.format("Baseline Version set to [%s]", baselineVersion));
			} else {
				LOG.info("No baseline version given. Exit");
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Check whether schema already contains version table
	 *
	 * @param flyway
	 * @return true, if version table is not found
	 */
	private boolean checkFlywayInitialized(final Flyway flyway) {
		final MigrationInfoService infoService = flyway.info();
		if (infoService.current() == null) {
			return false;
		}
		return true;
	}

	private void dumpMigrations(final MigrationInfoService infoService) {
		final MigrationInfo[] migrationInfos = infoService.all();

		final String separator = "+-------------+------------------------------------------+---------------------+-----------------+";
		LOG.info(separator);
		LOG.info("| Version     | Description                              | Installed on        | State           |");
		LOG.info(separator);

		if (migrationInfos.length == 0) {
			LOG.info("| No migrations applied yet                                            |");
		} else {
			for (int i = 0; i < migrationInfos.length; i++) {
				final MigrationInfo migrationInfo = migrationInfos[i];

				final StringBuilder buf = new StringBuilder();
				buf.append("| ");
				buf.append(StringUtils.leftPad(migrationInfo.getVersion().toString(), 11)).append(FIELD_SEPARATOR);
				buf.append(StringUtils.leftPad(migrationInfo.getDescription(), 40)).append(FIELD_SEPARATOR);
				final String installDate = migrationInfo.getInstalledOn().toString();
				buf.append(StringUtils.leftPad(installDate, 19)).append(FIELD_SEPARATOR);
				buf.append(StringUtils.leftPad(migrationInfo.getState().name(), 15)).append(FIELD_SEPARATOR);
				LOG.info(buf.toString());
			}
		}

		LOG.info(separator);
	}

}
