package com.fb.commons.flyway;

import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ProgrammOptionsParser {

    private static final String PROJECT_OPTION = "project";
    private static final String OPERATION_OPTION = "operation";
    private static final String STAGE_OPTION = "stage";
    private static final String ENCRYPT = "encrypt";

    /**
     * Parse and normalize the given command line arguments
     * 
     * @param args
     * @return
     */
    public ProgramOptions parseOptions(final String[] args) {
        // read command line
        final Options options = getOptions();
        final CommandLine commandLine = getCommandLine(args, options);
        // check if encrypted is set
        if (commandLine.hasOption(ENCRYPT)) {
            final ProgramOptions result = new ProgramOptions();
            result.setValue(commandLine.getOptionValue(ENCRYPT));
            return result;
        }
        // validate if options for migration is set in command line
        validateCommandLine(commandLine, options);
        return parseCommandLine(commandLine);
    }

    private Options getOptions() {
        final Options options = new Options();
        options.addOption(PROJECT_OPTION, true, "Project name");
        options.addOption(OPERATION_OPTION, true, "Type of operation: clean, migrate, info, validate, undo, baseline");
        options.addOption(STAGE_OPTION, true, "Stage name, one of the following TEST, INT, PROD");
        options.addOption(ENCRYPT, true, "Encrypts the given password");
        return options;
    }

    private CommandLine getCommandLine(final String[] args, final Options options) {
        final CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (final ParseException e) {
            throw new IllegalArgumentException("Error by command line parsing", e);
        }
    }

    /**
     * Checks if project, stage and operation option are set in command line
     * 
     * @param commandLine
     * @param options
     */
    private void validateCommandLine(final CommandLine commandLine, final Options options) {

        if (!commandLine.hasOption(STAGE_OPTION)) {
            printOptions(options);
            throw new IllegalArgumentException("Stage option not defined");
        }
        if (!commandLine.hasOption(PROJECT_OPTION)) {
            printOptions(options);
            throw new IllegalArgumentException("Project not defined");
        }
        if (!commandLine.hasOption(OPERATION_OPTION)) {
            printOptions(options);
            throw new IllegalArgumentException("Operation not defined");
        }

    }

    private void printOptions(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("options", options);
    }

    private ProgramOptions parseCommandLine(final CommandLine commandLine) {
        final ProgramOptions result = new ProgramOptions();

        result.setStage(commandLine.getOptionValue(STAGE_OPTION));
        result.setProject(commandLine.getOptionValue(PROJECT_OPTION).toLowerCase(Locale.US));
        result.setOperation(
                FlywayOperation.valueOf(commandLine.getOptionValue(OPERATION_OPTION).toUpperCase(Locale.US)));

        return result;
    }

}
