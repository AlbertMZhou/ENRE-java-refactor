package client;

import TempOutput.*;
import org.json.JSONArray;
import entity.properties.Relation;
import picocli.CommandLine;
import util.Tuple;
import visitor.relationInf.RelationInf;

import util.Configure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TemplateWork {

    protected static Configure configure = Configure.getConfigureInstance();

    public final void workflow(String[] args) {
//        String lang = args[0];
//        String inputDir = args[1];
//        String projectName = args[2];
//        String depMask = "111111111";
//        String aidlDir = null;
//        String hiddenDir = null;
//        if (args.length > 3) {
//            projectName = args[3];
//        }
//        if (args.length > 4) {
//            aidlDir = args[4];
//        }
//        if (args.length > 5){
//            hiddenDir = args[5];
//        }

        try {
            EnreCommand app = CommandLine.populateCommand(new EnreCommand(), args);
            if (app.help) {
                CommandLine.usage(new EnreCommand(), System.out);
                System.exit(0);
            }
            if(app.isjArcher()){
                System.out.println("this output is in jArcher version");
            }else {
                System.out.println("this output is in ENRE version");
            }
            executeCommand(app);
        } catch (Exception e) {
            if (e instanceof CommandLine.PicocliException) {
                CommandLine.usage(new EnreCommand(), System.out);
            } else if (e instanceof CommandLine.ParameterException){
                System.err.println(e.getMessage());
            }else {
                System.err.println("Exception encountered. If it is a design error, please report issue to us." );
                e.printStackTrace();
            }
            System.exit(0);
        }

    }

    public Map<Integer, ArrayList<Tuple<Integer, Relation>>> execute(EnreCommand app) {
        config("java", app.getSrc(), app.getProjectName());
        IdentifyEntities entityTreeBuilder;
        if (app.getAidl() != null) {
            if (app.getDir().length != 0) {
                entityTreeBuilder = new IdentifyEntities(
                        app.getSrc(),
                        app.getProjectName(),
                        app.getAidl(),
                        app.getDir());
            } else {
                entityTreeBuilder = new IdentifyEntities(
                        app.getSrc(),
                        app.getProjectName(),
                        app.getAidl()
                );
            }
        } else {
            if (app.getDir().length != 0) {
                entityTreeBuilder = new IdentifyEntities(
                        app.getSrc(),
                        app.getProjectName(),
                        app.getDir()
                );
            } else {
                entityTreeBuilder = new IdentifyEntities(
                        app.getSrc(),
                        app.getProjectName()
                );
            }
        }
        entityTreeBuilder.run();
        // identify external
        if (app.getExternal() != null) {
            ProcessThirdPartyMeth thirdPartyMeth = new ProcessThirdPartyMeth(app.getExternal(), "sheet1");
            thirdPartyMeth.convertExcelData();
        }
        //extract Deps
        IdentifyRelations entityDepAnalyzer = new IdentifyRelations();
        entityDepAnalyzer.run();

        JsonMap jsonMap = new JsonMap();
        Verification verify = new Verification();
        DependsString depends = new DependsString();
        summary();
        return jsonMap.getFinalRes();
    }

    public void executeCommand(EnreCommand app) throws Exception {
        String lang = app.getLang();
        String inputDir = app.getSrc();
        if(app.isjArcher()){
            inputDir=inputDir+"/src/main";//只分析main路径下
        }
        String projectName = app.getProjectName();
        String depMask = "111111111";
        String aidlDir = app.getAidl();
        String hiddenDir = app.getHidden();

        String[] additionDir = app.getDir();

        //Get the external
        String externalPath = app.getExternal();

        //Slim versin
        boolean slim = app.isSlim();

        config(lang, inputDir, projectName);
        String outputFile = configure.getAnalyzedProjectName()+ "-out";
        if (app.getOutputFile() != null){
            outputFile = app.getOutputFile();
        }

        String[] depTypes = getDepType(depMask);

        long startTime = System.currentTimeMillis();

        //identify Entities
        IdentifyEntities entityTreeBuilder;
        if(aidlDir != null){
            if (additionDir.length != 0){
                entityTreeBuilder = new IdentifyEntities(inputDir, projectName, aidlDir, additionDir);
            } else {
                entityTreeBuilder = new IdentifyEntities(inputDir, projectName, aidlDir);
            }
        }else {
            if (additionDir.length != 0){
                entityTreeBuilder = new IdentifyEntities(inputDir, projectName, additionDir);
            } else {
                entityTreeBuilder = new IdentifyEntities(inputDir, projectName);
            }
        }
        List<String> variables=entityTreeBuilder.run();

        // identify external
        if (externalPath != null){
            ProcessThirdPartyMeth thirdPartyMeth = new ProcessThirdPartyMeth(externalPath, "sheet1");
            thirdPartyMeth.convertExcelData();
        }

        //extract Deps
        IdentifyRelations entityDepAnalyzer = new IdentifyRelations();
        entityDepAnalyzer.run();

        long endTime = System.currentTimeMillis();
        System.out.println("\nConsumed time: " + (float) ((endTime - startTime) / 1000.00) + " s,  or " + (float) ((endTime - startTime) / 60000.00) + " min.\n");

        //build hierarchical dependencies
//        HiDeper hiDeper = new HiDeper();
//        hiDeper.run();
//
//        HiDepData hiDepData = HiDepData.getInstance();
//
//        Formator formator = new Formator(depTypes);
//        JDepObject jDepObject = formator.getfJsonDataModel();
//        XDepObject xDepObject = formator.getfXmlDataModel();
//
//        Csvgrapher csvgrapher = new Csvgrapher();
//        csvgrapher.buildProcess();
//        ArrayList<String[]> allNodes = csvgrapher.getNodes();
//        ArrayList<String[]> allEdges = csvgrapher.getEdges();
//
//        WriterIntf writer = new WriterIntf();
//        writer.run(jDepObject, xDepObject, allNodes, allEdges);

        JsonMap jsonMap = new JsonMap();
        Verification verify = new Verification();
        DependsString depends = new DependsString();
        //CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-Diango-out",configure.getAnalyzedProjectName()+ "-node", Django.nodeWriter());
        //CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-Diango-out",configure.getAnalyzedProjectName()+ "-edge", Django.edgeWriter(jsonMap.getFinalRes()));
        //specific-anti-
        // CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-enre-out",outputFile, JsonString.JSONWriteRelation(jsonMap.getFinalRes(), hiddenDir, slim));
        if(!app.isjArcher()){
            System.out.println("enre output");
            //输出，创建json文件，参数包括filePath输出文件路径名（文件夹，项目名称-enre-out）；fileName输出文件名称（分析项目名-out.json），jsonString输出内容。
            CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-enre-out",outputFile, JsonString.jsonWriteRelation(jsonMap.getFinalRes(), hiddenDir, slim));
            //JsonString.JSONWriteRelation()负责编写json字符串内容
            //jsonMap.getFinalRes()获取实体列表和依赖列表（依赖列表作为实体的属性） hiddenDir：hiddenapi-flag.csv文件路径, slim：是否精简输出
        }else {
            System.out.println("ja output");
            //判断是否为相对路径，进行绝对路径转换
            File file= new File(inputDir);
            String rootDir=file.getCanonicalPath();
//            System.out.println(file.exists()+":"+file.getCanonicalPath());
            CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-ja-out",outputFile, JsonString.JSONWriteRelationJA(jsonMap.getFinalRes(), rootDir, configure.getAnalyzedProjectName(),configure.getLang(),variables));
        }//        CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-enre-out",configure.getAnalyzedProjectName()+ "-hidden-not-match", ProcessHidden.getProcessHiddeninstance().outputResult());

        //CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-Diango-out",configure.getAnalyzedProjectName()+ "-imports", Verification.JSONWriteRela(verify.getRela()));
//        CreateFileUtil.createJsonFile(configure.getAnalyzedProjectName()+ "-enre-out",configure.getAnalyzedProjectName()+ "-generic-anti-out",
//                JSON.toJSONString(depends.getDependsString(projectName, inputDir, lang)));

        //output the summary of the acquired results.
        summary();
    }

    /**
     * parse the input parameter, save into configure
     *
     * @param inputDir
     * @param projectName
     */
    private void config(String lang, String inputDir, String projectName) {
        configure.setLang(lang);
        configure.setInputSrcPath(inputDir);
        configure.setAnalyzedProjectName(projectName);
        configure.setDefault();
    }


    private String[] getDepType(String depMask) {
        ArrayList<String> depStrs = new ArrayList<String>();
        for (int i = 0; i < depMask.toCharArray().length; i++) {
            if (depMask.toCharArray()[i] == '1') {
                if (i == 0) {
                    depStrs.add(Configure.RELATION_IMPORT);
                } else if (i == 1) {
                    depStrs.add(Configure.RELATION_INHERIT);
                } else if (i == 2) {
                    depStrs.add(Configure.RELATION_IMPLEMENT);
                } else if (i == 3) {
                    depStrs.add(Configure.RELATION_RETURN);
                } else if (i == 4) {
                    depStrs.add(Configure.RELATION_CALL);
                } else if (i == 5) {
                    depStrs.add(Configure.RELATION_SET);
                } else if (i == 6) {
                    depStrs.add(Configure.RELATION_USE);
                } else if (i == 7) {
                    depStrs.add(Configure.RELATION_PARAMETER);
                }
            }
        }
        return depStrs.toArray(new String[depStrs.size()]);
    }

    private void summary() {
        RelationInf relationInterface = new RelationInf();

        System.out.println("\nSummarize the entity's results:");
        System.out.println(relationInterface.entityStatis());
        System.out.println("\nSummarize the dependency's results:");
        System.out.println(relationInterface.dependencyStatis());
//        UndWriter undWriter = new UndWriter();
//        System.out.println(undWriter.priDepStatis()+ "\n");
    }


//    private void generateDataForExperiments(WriterIntf writer) {
//        //export formats consistent with understand, to compare with understand tool
//        writer.undTest();
//
//        //export external  implicit calls at file level as csv file
//        //writer.exportImplicitExternalAtFileLevel();
//
//        //export external implicit calls at file level
//        String[] partialDepType = new String[]{Configure.RELATION_IMPLICIT_EXTERNAL_CALL};
//        Formator partialFormator = new Formator(partialDepType);
//        JDepObject partialJDepObject = partialFormator.getfJsonDataModel();
//        JsonWriter jsonWriter = new JsonWriter();
//        String partialJsonfile = configure.getAnalyzedProjectName() + "_implicit_dep.json";
//        jsonWriter.toJson(partialJDepObject, partialJsonfile);
//        System.out.println("Export " + partialJsonfile);
//    }

}