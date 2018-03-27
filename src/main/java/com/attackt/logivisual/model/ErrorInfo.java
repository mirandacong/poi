package com.attackt.logivisual.model;

/**
 * 错误信息
 */
public class ErrorInfo {
    private String excelUid;
    private String excelFile;
    private String excelError;
    private String excelFormula;
    private String excelSheet;
    private String excelAddress;
    private String createTime;

    public ErrorInfo(String excelUid, String excelFile, String excelError, String excelFormula, String excelSheet, String excelAddress) {
        this.excelUid = excelUid;
        this.excelFile = excelFile;
        this.excelError = excelError;
        this.excelFormula = excelFormula;
        this.excelSheet = excelSheet;
        this.excelAddress = excelAddress;
    }

    public String getExcelUid() {
        return excelUid;
    }

    public void setExcelUid(String excelUid) {
        this.excelUid = excelUid;
    }

    public String getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(String excelFile) {
        this.excelFile = excelFile;
    }

    public String getExcelError() {
        return excelError;
    }

    public void setExcelError(String excelError) {
        this.excelError = excelError;
    }

    public String getExcelFormula() {
        return excelFormula;
    }

    public void setExcelFormula(String excelFormula) {
        this.excelFormula = excelFormula;
    }

    public String getExcelSheet() {
        return excelSheet;
    }

    public void setExcelSheet(String excelSheet) {
        this.excelSheet = excelSheet;
    }

    public String getExcelAddress() {
        return excelAddress;
    }

    public void setExcelAddress(String excelAddress) {
        this.excelAddress = excelAddress;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
