package com.uniinclusive.export;

/**
 * @Author: ghlin
 * @Date: 2020/3/20 16:50
 */
public class Project {
    private String prjClassId;
    private String prjClassCd;
    private String prjPath;
    private String prjName;

    public String getPrjClassId() {
        return prjClassId;
    }

    public void setPrjClassId(String prjClassId) {
        this.prjClassId = prjClassId;
    }

    public String getPrjClassCd() {
        return prjClassCd;
    }

    public void setPrjClassCd(String prjClassCd) {
        this.prjClassCd = prjClassCd;
    }

    public String getPrjPath() {
        return prjPath;
    }

    public void setPrjPath(String prjPath) {
        this.prjPath = prjPath;
    }

    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    @Override
    public String toString() {
        return "Project{" +
                "prjClassId='" + prjClassId + '\'' +
                ", prjClassCd='" + prjClassCd + '\'' +
                ", prjPath='" + prjPath + '\'' +
                ", prjName='" + prjName + '\'' +
                '}';
    }
}
