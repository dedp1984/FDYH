package com.nantian.domain;

public class ImportRecord extends ImportRecordKey {
    private String filename;

    private String operid;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOperid() {
        return operid;
    }

    public void setOperid(String operid) {
        this.operid = operid;
    }
}