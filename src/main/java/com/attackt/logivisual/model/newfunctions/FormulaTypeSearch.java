package com.attackt.logivisual.model.newfunctions;

/**
 * 搜索类函数
 */
public enum FormulaTypeSearch {
    ADDRESS(115),
    AREAS(116),
    CHOOSE(117),
    INDEX(118),
    INDIRECT(119),
    OFFSET(120),
    VLOOKUP(121),
    HLOOKUP(122),
    LOOKUP(123),
    MATCH(124);
    // 成员变量
    private int index;
    // 构造方法
    FormulaTypeSearch(int index) {
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
        for (FormulaTypeSearch temp: FormulaTypeSearch.values()) {
            if(temp.name().equals(str.toUpperCase()))
            {
                return true;
            }
        }
        return false;
    }
}
