package com.nantian.domain;

public class SysMenu {
    private String menuid;

    private String menuname;

    private String parentmenuid;

    private String pageurl;

    private String isaction;

    private String actionname;

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }

    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }

    public String getParentmenuid() {
        return parentmenuid;
    }

    public void setParentmenuid(String parentmenuid) {
        this.parentmenuid = parentmenuid;
    }

    public String getPageurl() {
        return pageurl;
    }

    public void setPageurl(String pageurl) {
        this.pageurl = pageurl;
    }

    public String getIsaction() {
        return isaction;
    }

    public void setIsaction(String isaction) {
        this.isaction = isaction;
    }

    public String getActionname() {
        return actionname;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }
}