package com.uniinclusive.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ghlin
 * @Date: 2020/3/21 15:36
 */
public class ExportMain {
    private static final String FILE_PATH = "E:\\devIn\\condInput.xlsx";
    private static String[] FIELDS = new String[]{"prjClassId", "prjClassCd", "prjPath", "prjName"};
    private static String[] FORMULA_FIELDS = new String[]{"prjPath", "paramsCd", "paramsId", "paramsName", "isCond"};
    private static Map<String, Project> allProjects = new HashMap<>();
    private static Map<String, FormulaParams> allParams = new HashMap<>();
    private static Map<String, InputCond> allInputCondByCode = new HashMap<>();
    private static Map<String, InputCond> allInputCondById = new HashMap<>();
    private static Map<String, List<InputCondValue>> allInputCondValue = new HashMap<>();

    private static final String[] INPUT_FIELDS = new String[]{"prjPath", "objValue", "seq", "parentSeq", "objType", "showType", "repet"};
    private static final String[] COND_FIELDS = new String[]{"id", "code", "name", "seq"};
    private static final String[] COND_VALUE_FIELDS = new String[]{"id", "condId", "name"};

    public static void main(String[] args) {
        //数据加载
        List<Project> projects = ReadOrWriteUtils.readExcel(FILE_PATH, "Sheet1", FIELDS, Project.class);
        for (Project project : projects) {
            allProjects.put(project.getPrjPath(), project);
        }
        //参数表
        List<FormulaParams> allFormulaParams = ReadOrWriteUtils.readExcel(FILE_PATH, "Sheet3", FORMULA_FIELDS, FormulaParams.class);
        for (FormulaParams formulaParams : allFormulaParams) {
            allParams.put(formulaParams.getParamsCd(), formulaParams);
        }
        // List<FormulaParams> formulaParams = ReadOrWriteUtils.readExcel(FILE_PATH, "Sheet2", FORMULA_FIELDS, FormulaParams.class);
        List<InputDataVO> inputDataVOList = ReadOrWriteUtils.readExcel(FILE_PATH, "Sheet4", INPUT_FIELDS, InputDataVO.class);
        List<InputCond> inputCondList = ReadOrWriteUtils.readExcel(FILE_PATH, "Sheet6", COND_FIELDS, InputCond.class);
        for (InputCond inputCond : inputCondList) {
            allInputCondByCode.put(inputCond.getCode(), inputCond);
            allInputCondById.put(inputCond.getId(), inputCond);
        }
        List<InputCondValue> inputCondValueList = ReadOrWriteUtils.readExcel(FILE_PATH, "Sheet7", COND_VALUE_FIELDS, InputCondValue.class);
        for (InputCondValue inputCondValue : inputCondValueList) {
            InputCond inputCond = allInputCondById.get(inputCondValue.getCondId());
            String condCode = inputCond.getCode();
            if (!allInputCondValue.containsKey(condCode)) {
                allInputCondValue.put(condCode, new ArrayList<>());
            }
            allInputCondValue.get(condCode).add(inputCondValue);
        }
        for (InputDataVO inputDataVO : inputDataVOList) {
            if (allProjects.containsKey(inputDataVO.getPrjPath())) {
                inputDataVO.setPrjId(allProjects.get(inputDataVO.getPrjPath()).getPrjClassId());
            }
        }
        Generate generate = new Generate(inputDataVOList);
        // 生成条件
        generate.generateCond();
        generate.generateCondValue();
        generate.generateRlt(allParams, allInputCondByCode, allInputCondValue);
        System.out.println("-------------运行结束-------------");
    }


}
