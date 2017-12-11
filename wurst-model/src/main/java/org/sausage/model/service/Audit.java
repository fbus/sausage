package org.sausage.model.service;

public class Audit {

    public enum EnableAuditing {
        NEVER, ALWAYS, WHEN_TOP_LEVEL_ONLY
    }

    public enum LogOn {
        ERROR_ONLY, ERROR_AND_SUCCESS, ERROR_AND_SUCCESS_AND_START
    }

    public enum IncludePipeline {
        NEVER, ON_ERROR_ONLY, ALWAYS
    }

    public EnableAuditing when;

    public IncludePipeline includePipeline;

}
