package com.nantian.domain;

public class BindAccountToManager extends BindAccountToManagerKey {
    private Double percent;

    private String type;

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}