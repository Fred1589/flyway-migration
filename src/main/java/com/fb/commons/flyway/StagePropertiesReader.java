package com.fb.commons.flyway;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

public final class StagePropertiesReader {

    private static final Logger LOG = Logger.getLogger(StagePropertiesReader.class.getSimpleName());

    private StagePropertiesReader() {
        // private constructor
    }

    public static StagePropertiesReader build() {
        return new StagePropertiesReader();
    }

    /**
     * Read common, stage and system properties and returns a merge of them.
     * 
     * @param options
     */
    public StageProperties readEnviromentProperties(final ProgramOptions options) {
        final String projectName = options.getProject();

        // first read common properties
        final String commonConfigFile = getCommonConfigFileName(projectName);
        final Properties stageProperties = readPropertiesFromClasspath(commonConfigFile);

        // merge stage specific properties
        final String stageConfigFile = getStageConfigFileName(options);
        stageProperties.putAll(readPropertiesFromClasspath(stageConfigFile));

        // merge system properties
        final Properties systemProperties = getSystemProperties(projectName);
        stageProperties.putAll(systemProperties);

        return new StageProperties(stageProperties, projectName);
    }

    @SuppressWarnings("rawtypes")
    private Properties getSystemProperties(String project) {
        final Properties systemProperties = System.getProperties();
        final Properties result = new Properties();
        for (Enumeration e = systemProperties.propertyNames(); e.hasMoreElements();) {
            final String propertyName = (String) e.nextElement();
            if (propertyName.startsWith(project)) {
                result.put(propertyName, systemProperties.getProperty(propertyName));
            }
        }
        return result;
    }

    private String getCommonConfigFileName(final String executionContext) {
        return "config/COMMON/" + executionContext + "-env.properties";
    }

    private String getStageConfigFileName(final ProgramOptions options) {
        return "config/" + options.getStage() + "/" + options.getProject() + "-env.properties";
    }

    private Properties readPropertiesFromClasspath(final String fileName) {
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
        if (inputStream == null) {
            LOG.warning("Could not find properties: " + fileName);
            return null;
        }

        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (final IOException e) {
            throw new MigrationException("Could not read properties: " + fileName, e);
        }
        return properties;
    }

}
