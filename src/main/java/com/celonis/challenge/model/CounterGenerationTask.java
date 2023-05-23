package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;


@Entity
public class CounterGenerationTask extends ProjectGenerationTask {

    private Integer x;
    private Integer y;

    @JsonIgnore
    private Integer progress;


    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
