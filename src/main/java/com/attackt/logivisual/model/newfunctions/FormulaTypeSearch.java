package com.attackt.logivisual.model.newfunctions;

/**
 * 搜索类函数
 */
public enum FormulaTypeSearch {
    ADDRESS(116),
    AREAS(117),
    CHOOSE(118),
    INDEX(119),
    INDIRECT(120),
    OFFSET(121),
    VLOOKUP(122),
    HLOOKUP(123),
    LOOKUP(124),
    MATCH(125);
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
