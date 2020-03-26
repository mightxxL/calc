package com.uniinclusive.export;

/**
 * @Author: ghlin
 * @Date: 2020/3/20 16:55
 */
public class OutPutCond {
    private String prjPath;
    private String condId;
    private String condCd;
    private String condName;

    public String getCondId() {
        return condId;
    }

    public void setCondId(String condId) {
        this.condId = condId;
    }

    public String getCondName() {
        return condName;
    }

    public void setCondName(String condName) {
        this.condName = condName;
    }

    public String getPrjPath() {
        return prjPath;
    }

    public void setPrjPath(String prjPath) {
        this.prjPath = prjPath;
    }

    public String getCondCd() {
        return condCd;
    }

    public void setCondCd(String condCd) {
        this.condCd = condCd;
    }
}
