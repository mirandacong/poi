package com.attackt.logivisual.model.newfunctions;

/**
 * B4类型
 */
public enum FormulaTypeB4 {
    IF(126);
    // 成员变量
    private int index;
    // 构造方法
    FormulaTypeB4(int index) {
        this.index = index;
    }
    //覆盖方法
    @Override
    public String toString() {
        return this.index+"";
    }
    /**
     * 搜索
     * @param str
     * @return 返回值
     */
    public static boolean isContainStr(String str)
    {
        for (FormulaTypeB4 temp: FormulaTypeB4.values()) {
            if(temp.name().equals(str.toUpperCase()))
            {
                return true;
            }
        }
        return false;
    }
}
