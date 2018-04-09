package com.attackt.logivisual.model.newfunctions;

import org.apache.poi.ss.formula.ptg.*;

/**
 * 自定义类型
 */
public enum FormulaTypeB5 {
    ;
    /**
     * 搜索
     * @param ptgs
     * @return 返回值
     */
    public static boolean isContainPtg(Ptg[] ptgs)
    {
        if (ptgs.length >= 1)
        {
            Ptg ptg = ptgs[0];
            if(ptg instanceof NameXPxg)
            {
                return true;
            }
        }
        return false;
    }
}
