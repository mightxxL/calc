package com.uniinclusive.export;

/**
 * @Author: ghlin
 * @Date: 2020/3/20 16:52
 */
public class FormulaParams {
    private String prjPath;
    private String paramsCd;
    private String paramsId;
    private String isCond;
    private String paramsName;

    public String getPrjPath() {
        return prjPath;
    }

    public void setPrjPath(String prjPath) {
        this.prjPath = prjPath;
    }

    public String getParamsCd() {
        return paramsCd;
    }

    public void setParamsCd(String paramsCd) {
        this.paramsCd = paramsCd;
    }

    public String getParamsId() {
        return paramsId;
    }

    public void setParamsId(String paramsId) {
        this.paramsId = paramsId;
    }

    public String getIsCond() {
        return isCond;
    }

    public void setIsCond(String isCond) {
        this.isCond = isCond;
    }

    public String getParamsName() {
        return paramsName;
    }

    public void setParamsName(String paramsName) {
        this.paramsName = paramsName;
    }

    @Override
    public String toString() {
        return "FormulaParams{" +
                "prjPath='" + prjPath + '\'' +
                ", paramsCd='" + paramsCd + '\'' +
                ", paramsId='" + paramsId + '\'' +
                ", isCond='" + isCond + '\'' +
                ", paramsName='" + paramsName + '\'' +
                '}';
    }
}
