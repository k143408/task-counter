package com.celonis.challenge.response;

public class TaskProgressResponse {
    private final Integer progress;
    public TaskProgressResponse(Integer progress) {
        this.progress = progress;
    }

    public Integer getProgress() {
        return progress;
    }
}
