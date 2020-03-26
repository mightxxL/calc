package com.uniinclusive.export;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ghlin
 * @Date: 2020/3/21 20:36
 */
public class Generate {
    Logger logger = LoggerFactory.getLogger(Generate.class);

    private static final String outFilePath = "E:\\devIn\\condOutput.xlsx";
    private static final String COND_ID_FORMAT = "SM_COND_203_19%s_000%s";
    private static final String COND_CD_FORMAT = "COND19%s%s";
    private static final String[] WRITE_COND_FIELDS = new String[]{"condId", "condCd", "condName"};
    private Map<String, InputDataVO> inputDataVOMap = new LinkedHashMap<>();
    private static final String RLT_ID_FORMAT = "SM_VIEW_RLT_203_9%s_0%s";
    private Map<String, Integer> rltCountMap = new HashMap<>();


    public Generate(List<InputDataVO> inputDataVOList) {
        for (InputDataVO inputDataVO : inputDataVOList) {
            inputDataVOMap.put(inputDataVO.getSeq(), inputDataVO);
        }
    }

    public void generateCond() {
        Collection<InputDataVO> inputDataVOList = inputDataVOMap.values();
        if (inputDataVOList != null) {
            List<InputDataVO> condInputs = inputDataVOList.stream().filter(x -> StringUtils.equals(x.getObjType(), "0")).collect(Collectors.toList());
            List<OutPutCond> outPutCondList = new ArrayList<>();
            Map<String, Integer> countMap = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (InputDataVO dataVO : condInputs) {
                OutPutCond outPutCond = new OutPutCond();
                outPutCond.setPrjPath(dataVO.getPrjPath());
                outPutCond.setCondName(dataVO.getObjValue());
                String prjPath = outPutCond.getPrjPath();
                String tail = joinFormat(countMap, prjPath, sb);
                String condId = String.format(COND_ID_FORMAT, sb.toString(), tail);
                String condCd = String.format(COND_CD_FORMAT, sb.toString(), tail);
                outPutCond.setCondId(condId);
                outPutCond.setCondCd(condCd);
                outPutCondList.add(outPutCond);
                sb.setLength(0);
                dataVO.setCondId(condId);
            }
            ReadOrWriteUtils.writeToExcel(outFilePath, outPutCondList, WRITE_COND_FIELDS);
        }
    }

    private static String preZeroTo(String oldStr, int allLength) {
        int length = oldStr.length();
        int diff = allLength - length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diff; i++) {
            sb.append("0");
        }
        sb.append(oldStr);
        return sb.toString();
    }

    private static final String outCondValuePath = "E:\\devIn\\condOutput2.xlsx";
    private Map<String, Integer> condValueCountMap = new HashMap<>();
    private static final String COND_VALUE_ID_FORMAT = "SM_CONDVAL_203_19%s_00%s";
    private static final String[] WRITE_COND_VALUE_FIELDS = new String[]{"condValueId", "condId", "condValueStr"};

    public void generateCondValue() {
        Collection<InputDataVO> inputDataVOList = inputDataVOMap.values();
        if (inputDataVOList == null) {
            return;
        }
        List<OutPutCondValue> condValues = new ArrayList<>();
        for (InputDataVO inputDataVO : inputDataVOList) {
            /*if (StringUtils.equals(inputDataVO.getObjType(), "0")) {
                String seq = inputDataVO.getSeq();
                boolean anyMatch = inputDataVOList.stream().anyMatch(x -> StringUtils.equals(seq, x.getParentSeq()));
                if (anyMatch) {
                    continue;
                }
                condValues.add(instanceCondValue(inputDataVO, inputDataVO.getCondId()));
            } else*/
            if (StringUtils.equals(inputDataVO.getObjType(), "1")) {
                String parentSeq = inputDataVO.getParentSeq();
                if (inputDataVOMap.containsKey(parentSeq)) {
                    InputDataVO parent = inputDataVOMap.get(parentSeq);
                    condValues.add(instanceCondValue(inputDataVO, parent.getCondId()));
                }
            }
        }
        ReadOrWriteUtils.writeToExcel(outCondValuePath, condValues, WRITE_COND_VALUE_FIELDS);
    }

    private OutPutCondValue instanceCondValue(InputDataVO inputDataVO, String condId) {
        OutPutCondValue outPutCondValue = new OutPutCondValue();
        outPutCondValue.setCondId(condId);
        outPutCondValue.setCondValueStr(inputDataVO.getObjValue());

        String condValueId = getFormatId(inputDataVO.getPrjPath(), condValueCountMap, COND_VALUE_ID_FORMAT);
        outPutCondValue.setCondValueId(condValueId);
        inputDataVO.setCondValueId(condValueId);
        return outPutCondValue;
    }

    /**
     * 关系表
     */
    private static final String outRltPath = "E:\\devIn\\condOutput3.xlsx";
    private static final String[] WRITE_RLT_FIELDS = new String[]{"rltId", "prjClassId", "parentRltId", "objId", "objName", "objType", "showType", "repet"};

    public void generateRlt(Map<String, FormulaParams> allParams,
                            Map<String, InputCond> allInputConds,
                            Map<String, List<InputCondValue>> allInputCondValues) {
        Collection<InputDataVO> inputDataVOList = inputDataVOMap.values();
        if (inputDataVOList == null) {
            return;
        }
        List<OutPutRlt> outPutRlts = new ArrayList<>();
        for (InputDataVO inputDataVO : inputDataVOList) {
            String rltId = getFormatId(inputDataVO.getPrjPath(), rltCountMap, RLT_ID_FORMAT);
            inputDataVO.setRltId(rltId);
        }
        for (InputDataVO dataVO : inputDataVOList) {
            instanceRlt(outPutRlts, dataVO, allParams, allInputConds, allInputCondValues);
        }
        ReadOrWriteUtils.writeToExcel(outRltPath, outPutRlts, WRITE_RLT_FIELDS);
    }

    private void instanceRlt(List<OutPutRlt> outPutRlts,
                             InputDataVO inputDataVO,
                             Map<String, FormulaParams> paramsMap,
                             Map<String, InputCond> allInputConds,
                             Map<String, List<InputCondValue>> allInputCondValues) {
        OutPutRlt outPutRlt = new OutPutRlt();
        outPutRlt.setRltId(inputDataVO.getRltId());
        outPutRlt.setPrjClassId(inputDataVO.getPrjId());
        outPutRlt.setShowType(inputDataVO.getShowType());
        if (StringUtils.isBlank(inputDataVO.getRepet())) {
            logger.error(inputDataVO.getSeq() + "-重复次数为空。");
            outPutRlt.setRepet("1");
        } else {
            outPutRlt.setRepet(inputDataVO.getRepet());
        }
        if (!StringUtils.isBlank(inputDataVO.getParentSeq())) {
            String parentRltId = inputDataVOMap.get(inputDataVO.getParentSeq()).getRltId();
            outPutRlt.setParentRltId(parentRltId);
        }
        String objType = inputDataVO.getObjType();
        if (objType == null) {
            logger.error(inputDataVO.getSeq() + "-实体类型为空。");
            return;
        }
        switch (objType) {
            case "0":
                outPutRlt.setObjId(inputDataVO.getCondId());
                outPutRlt.setObjName(inputDataVO.getObjValue());
                outPutRlt.setObjType("00");
                break;
            case "1":
                outPutRlt.setObjId(inputDataVO.getCondValueId());
                outPutRlt.setObjName(inputDataVO.getObjValue());
                outPutRlt.setObjType("01");
                break;
            case "2":
                String paramCd = inputDataVO.getObjValue();
                if (paramsMap.containsKey(paramCd)) {
                    FormulaParams formulaParams = paramsMap.get(paramCd);
                    // 系数跳过
                    if (StringUtils.equals(formulaParams.getIsCond(), "1")) {
                        return;
                    }
                    outPutRlt.setObjId(formulaParams.getParamsId());
                    outPutRlt.setObjName(formulaParams.getParamsName());
                } else {
                    logger.error(inputDataVO.getSeq() + ":" + inputDataVO.getObjValue() + "-无法找到参数！");
                }
                outPutRlt.setObjType("02");
                break;
            case "3":
                String condCode = inputDataVO.getObjValue();
                if (allInputConds.containsKey(condCode)) {
                    InputCond inputCond = allInputConds.get(condCode);
                    outPutRlt.setObjId(inputCond.getId());
                    outPutRlt.setObjName(inputCond.getName());
                    if (allInputCondValues.containsKey(condCode)) {
                        for (InputCondValue condValue : allInputCondValues.get(condCode)) {
                            outPutRlts.add(instanceCondValueByCond(inputDataVO.getPrjId(), inputDataVO.getRltId(), inputDataVO.getPrjPath(), condValue));
                        }
                    }
                }
                outPutRlt.setObjType("00");
                break;
            case "4":
                outPutRlt.setObjName(inputDataVO.getObjValue());
                outPutRlt.setObjType("03");
                break;
            default:
        }
        outPutRlts.add(outPutRlt);
    }

    private String getFormatId(String prjPath, Map<String, Integer> rltCountMap, String rltIdFormat) {
        StringBuilder sb = new StringBuilder();
        String tail = joinFormat(rltCountMap, prjPath, sb);
        return String.format(rltIdFormat, sb.toString(), tail);
    }

    private String joinFormat(Map<String, Integer> rltCountMap, String prjPath, StringBuilder sb) {
        String[] nodeNums = prjPath.split("\\.");
        for (String nodeNum : nodeNums) {
            sb.append(preZeroTo(nodeNum, 2));
        }
        int diff = 10 - sb.length();
        for (int j = 0; j < diff; j++) {
            sb.append("0");
        }
        if (rltCountMap.containsKey(prjPath)) {
            Integer nowCount = rltCountMap.get(prjPath);
            rltCountMap.put(prjPath, ++nowCount);
        } else {
            rltCountMap.put(prjPath, 1);
        }
        return preZeroTo(String.valueOf(rltCountMap.get(prjPath)), 2);
    }

    private OutPutRlt instanceCondValueByCond(String prjClassId, String parentRltId, String prjPath, InputCondValue inputCondValue) {
        OutPutRlt outPutRlt = new OutPutRlt();
        String rltId = getFormatId(prjPath, rltCountMap, RLT_ID_FORMAT);
        outPutRlt.setRltId(rltId);
        outPutRlt.setParentRltId(parentRltId);
        outPutRlt.setPrjClassId(prjClassId);
        outPutRlt.setObjId(inputCondValue.getId());
        outPutRlt.setObjName(inputCondValue.getName());
        outPutRlt.setObjType("01");
        outPutRlt.setRepet("1");
        return outPutRlt;
    }
}
