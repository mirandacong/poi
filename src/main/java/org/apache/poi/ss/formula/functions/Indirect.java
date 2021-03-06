/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.ss.formula.functions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.attackt.logivisual.model.newfunctions.SourceNodeType;
import com.attackt.logivisual.model.newfunctions.SourceValueType;
import com.attackt.logivisual.mysql.OperationUtils;
import com.attackt.logivisual.utils.ThreadUtil;
import org.apache.poi.ss.formula.*;
import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.util.CellReference;

import javax.xml.bind.SchemaOutputResolver;
import java.util.Map;

/**
 * Implementation for Excel function INDIRECT<p>
 * <p>
 * INDIRECT() returns the cell or area reference denoted by the text argument.<p>
 * <p>
 * <b>Syntax</b>:</br>
 * <b>INDIRECT</b>(<b>ref_text</b>,isA1Style)<p>
 * <p>
 * <b>ref_text</b> a string representation of the desired reference as it would
 * normally be written in a cell formula.<br>
 * <b>isA1Style</b> (default TRUE) specifies whether the ref_text should be
 * interpreted as A1-style or R1C1-style.
 *
 * @author Josh Micich
 */
public final class Indirect implements FreeRefFunction {

    public static final FreeRefFunction instance = new Indirect();

    private Indirect() {
        // enforce singleton
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }

        boolean isA1style;
        String text;
        try {
            ValueEval ve = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec
                    .getColumnIndex());
            text = OperandResolver.coerceValueToString(ve);
            switch (args.length) {
                case 1:
                    isA1style = true;
                    break;
                case 2:
                    isA1style = evaluateBooleanArg(args[1], ec);
                    break;
                default:
                    return ErrorEval.VALUE_INVALID;
            }
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        ValueEval valueEval1 = evaluateIndirect(ec, text, isA1style);
        //-------处理数据开始---------
        try {
            ValueEval valueEval;
            if (ec.isSingleValue() && !(valueEval1 instanceof ErrorEval)) {
                valueEval = OperandResolver.getSingleValue(valueEval1, ec.getRowIndex(), ec.getColumnIndex());
            } else {
                valueEval = valueEval1;
            }
            String excelId = new ThreadUtil().getExcelUid();
            int funcValueType = Integer.parseInt(SourceValueType.valueOf(valueEval.getClass().getSimpleName()).toString());
            String funcValue = "";
            if (valueEval instanceof NumberEval) {
                NumberEval ne = (NumberEval) valueEval;
                funcValue = String.valueOf(ne.getNumberValue());
            }
            if (valueEval instanceof BoolEval) {
                BoolEval be = (BoolEval) valueEval;
                funcValue = String.valueOf(be.getBooleanValue());
            }
            if (valueEval instanceof StringEval) {
                StringEval ne = (StringEval) valueEval;
                funcValue = String.valueOf(ne.getStringValue());
            }
            if (valueEval instanceof ErrorEval) {
                funcValue = ErrorEval.getText(((ErrorEval) valueEval).getErrorCode());
            }
            // 查找对应的记录
            OperationUtils operationUtils = new OperationUtils();
            Map<String, Object> map = operationUtils.findData(excelId);
            if (map.size() > 0) {
                String text1 = map.get("content").toString();
                Integer recordId = Integer.valueOf(map.get("id").toString());
                //
                JSONArray jsonArray = JSONArray.parseArray(text1);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                jsonObject.put("funcValueType", funcValueType);
                jsonObject.put("funcValue", funcValue);
                if (!(valueEval instanceof ErrorEval)) {
                    // 添加新的
                    CellReference cellReference = new CellReference(text);
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("nodeType", Integer.parseInt(SourceNodeType.valueOf("RefPtg").toString()));
                    newJsonObject.put("nodeAttr", cellReference.formatAsString());
                    newJsonObject.put("numArgs", 0);
                    newJsonObject.put("sheetIndex", ((LazyRefEval) valueEval1).getFirstSheetIndex());
                    // 连接旧的
                    JSONArray jsonArray1 = new JSONArray();
                    jsonArray1.add(newJsonObject);
                    jsonObject.put("para_info", jsonArray1);
                }
                // 更改有效性数据
                operationUtils.updateData(recordId, jsonArray.toJSONString());
            }
        } catch (Exception e) {
            System.out.println(getClass().getName() + " 函数内部重算出错 " + e);
        }
        //-------处理数据结束---------
        return valueEval1;
    }

    private static boolean evaluateBooleanArg(ValueEval arg, OperationEvaluationContext ec)
            throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, ec.getRowIndex(), ec.getColumnIndex());

        if (ve == BlankEval.instance || ve == MissingArgEval.instance) {
            return false;
        }
        // numeric quantities follow standard boolean conversion rules
        // for strings, only "TRUE" and "FALSE" (case insensitive) are valid
        return OperandResolver.coerceValueToBoolean(ve, false).booleanValue();
    }

    private static ValueEval evaluateIndirect(final OperationEvaluationContext ec, String text,
                                              boolean isA1style) {

        // Search backwards for '!' because sheet names can contain '!'
        int plingPos = text.lastIndexOf('!');

        String workbookName;
        String sheetName;
        String refText; // whitespace around this gets trimmed OK
        if (plingPos < 0) {
            workbookName = null;
            sheetName = null;
            refText = text;
        } else {
            String[] parts = parseWorkbookAndSheetName(text.subSequence(0, plingPos));
            if (parts == null) {
                return ErrorEval.REF_INVALID;
            }
            workbookName = parts[0];
            sheetName = parts[1];
            refText = text.substring(plingPos + 1);
        }

        if (Table.isStructuredReference.matcher(refText).matches()) {
            // The argument is structured reference
            Area3DPxg areaPtg = null;
            try {
                areaPtg = FormulaParser.parseStructuredReference(refText, (FormulaParsingWorkbook) ec.getWorkbook(), ec.getRowIndex());
            } catch (FormulaParseException e) {
                return ErrorEval.REF_INVALID;
            }
            return ec.getArea3DEval(areaPtg);
        } else {
            // The argument is regular reference
            String refStrPart1;
            String refStrPart2;
            int colonPos = refText.indexOf(':');
            if (colonPos < 0) {
                refStrPart1 = refText.trim();
                refStrPart2 = null;
            } else {
                refStrPart1 = refText.substring(0, colonPos).trim();
                refStrPart2 = refText.substring(colonPos + 1).trim();
            }
            return ec.getDynamicReference(workbookName, sheetName, refStrPart1, refStrPart2, isA1style);
        }
    }

    /**
     * @return array of length 2: {workbookName, sheetName,}.  Second element will always be
     * present.  First element may be null if sheetName is unqualified.
     * Returns <code>null</code> if text cannot be parsed.
     */
    private static String[] parseWorkbookAndSheetName(CharSequence text) {
        int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return null;
        }
        if (canTrim(text)) {
            return null;
        }
        char firstChar = text.charAt(0);
        if (Character.isWhitespace(firstChar)) {
            return null;
        }
        if (firstChar == '\'') {
            // workbookName or sheetName needs quoting
            // quotes go around both
            if (text.charAt(lastIx) != '\'') {
                return null;
            }
            firstChar = text.charAt(1);
            if (Character.isWhitespace(firstChar)) {
                return null;
            }
            String wbName;
            int sheetStartPos;
            if (firstChar == '[') {
                int rbPos = text.toString().lastIndexOf(']');
                if (rbPos < 0) {
                    return null;
                }
                wbName = unescapeString(text.subSequence(2, rbPos));
                if (wbName == null || canTrim(wbName)) {
                    return null;
                }
                sheetStartPos = rbPos + 1;
            } else {
                wbName = null;
                sheetStartPos = 1;
            }

            // else - just sheet name
            String sheetName = unescapeString(text.subSequence(sheetStartPos, lastIx));
            if (sheetName == null) { // note - when quoted, sheetName can
                // start/end with whitespace
                return null;
            }
            return new String[]{wbName, sheetName,};
        }

        if (firstChar == '[') {
            int rbPos = text.toString().lastIndexOf(']');
            if (rbPos < 0) {
                return null;
            }
            CharSequence wbName = text.subSequence(1, rbPos);
            if (canTrim(wbName)) {
                return null;
            }
            CharSequence sheetName = text.subSequence(rbPos + 1, text.length());
            if (canTrim(sheetName)) {
                return null;
            }
            return new String[]{wbName.toString(), sheetName.toString(),};
        }
        // else - just sheet name
        return new String[]{null, text.toString(),};
    }

    /**
     * @return <code>null</code> if there is a syntax error in any escape sequence
     * (the typical syntax error is a single quote character not followed by another).
     */
    private static String unescapeString(CharSequence text) {
        int len = text.length();
        StringBuilder sb = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            char ch = text.charAt(i);
            if (ch == '\'') {
                // every quote must be followed by another
                i++;
                if (i >= len) {
                    return null;
                }
                ch = text.charAt(i);
                if (ch != '\'') {
                    return null;
                }
            }
            sb.append(ch);
            i++;
        }
        return sb.toString();
    }

    private static boolean canTrim(CharSequence text) {
        int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return false;
        }
        if (Character.isWhitespace(text.charAt(0))) {
            return true;
        }
        if (Character.isWhitespace(text.charAt(lastIx))) {
            return true;
        }
        return false;
    }
}
