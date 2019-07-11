package com.fb.commons.flyway;

public class ProgramOptions {

    // Operation
    private FlywayOperation operation;
    // Project
    private String project;
    // Stage
    private String stage;

    // Value to encrypt
    private String value;

    public FlywayOperation getOperation() {
        return operation;
    }

    public void setOperation(final FlywayOperation operation) {
        this.operation = operation;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(final String stage) {
        this.stage = stage;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
