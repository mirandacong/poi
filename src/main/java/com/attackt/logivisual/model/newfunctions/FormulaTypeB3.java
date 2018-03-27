package com.attackt.logivisual.model.newfunctions;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.RefPtg;

/**
 * B3类型
 */
public enum FormulaTypeB3 {
    ;
    /**
     * 搜索
     * @param ptgs
     * @return 返回值
     */
    public static boolean isContainPtg(Ptg[] ptgs)
    {
        if (ptgs.length == 1)
        {
            Ptg ptg = ptgs[0];
            if(ptg instanceof RefPtg || ptg instanceof Ref3DPxg)
            {
                return true;
            }
        }
        return false;
    }
}
