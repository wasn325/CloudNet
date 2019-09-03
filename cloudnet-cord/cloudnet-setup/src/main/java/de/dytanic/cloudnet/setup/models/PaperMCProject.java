package de.dytanic.cloudnet.setup.models;

public class PaperMCProject {

    private final String project;
    private final String[] versions;

    public PaperMCProject(final String project, final String[] versions) {
        this.project = project;
        this.versions = versions;
    }

    public String getProject() {
        return project;
    }

    public String[] getVersions() {
        return versions;
    }
}
