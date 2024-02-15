package org.example;

import com.hpe.adm.nga.sdk.Octane;
import com.hpe.adm.nga.sdk.authentication.Authentication;
import com.hpe.adm.nga.sdk.authentication.SimpleClientAuthentication;
import com.hpe.adm.nga.sdk.entities.EntityList;
import com.hpe.adm.nga.sdk.entities.OctaneCollection;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.nga.sdk.query.Query;
import com.hpe.adm.nga.sdk.query.QueryMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) {
        Authentication clientAuthentication = new SimpleClientAuthentication("sa@nga", "Welcome1");
        final Octane.Builder octaneBuilder = new Octane.Builder(clientAuthentication);

        octaneBuilder.Server("http://localhost:8080/dev");
        octaneBuilder.sharedSpace(1001);
        octaneBuilder.workSpace(11004);

        Octane octane = octaneBuilder.build();

        String filename = args[0];
        List<Test> tests = new ArrayList<>();
        try {
            tests = parseJsonTests(filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


//        tests.forEach(test -> updateTestById(test, octane));
        updateTestsById(tests, octane);
    }

    private static List<Test> parseJsonTests(String filename) throws Exception {
        List<Test> tests = new ArrayList<>();
        Object obj = new JSONParser().parse(new FileReader(filename));
        JSONObject jo = (JSONObject) obj;
        JSONArray testsJson = (JSONArray) jo.get("tests");

        for (Object o : testsJson) {
            Map testObj = ((Map) o);
            tests.add(new Test((String) testObj.get("id"), (String) testObj.get("name"), (String) testObj.get("class_name"),
                    (String) testObj.get("package")));
        }

        return tests;

    }

    private static void updateTestById(Test test, Octane octane) {
        EntityList automatedTestsEntityList = octane.entityList("automated_tests");

        final Query query = Query.statement("id", QueryMethod.EqualTo, test.getId()).build();
        OctaneCollection<EntityModel> automatedTests =
                automatedTestsEntityList.get().addFields("name", "class_name", "package").query(query).execute();
        Optional<EntityModel> automatedTestOpt = automatedTests.stream().findFirst();
        if (automatedTestOpt.isPresent()) {
            EntityModel automatedTest = automatedTestOpt.get();
            automatedTest.setValue(new StringFieldModel("name", test.getName()));
            automatedTest.setValue(new StringFieldModel("class_name", test.getClassName()));
            automatedTest.setValue(new StringFieldModel("package", test.getTestPackage()));

            automatedTestsEntityList.update().entities(Collections.singleton(automatedTest)).execute();

            System.out.println("Test updated");
        } else {
            System.out.println("Test not found!");
        }
    }

    private static void updateTestsById(List<Test> tests, Octane octane) {
        EntityList automatedTestsEntityList = octane.entityList("automated_tests");

        final Query query =
                Query.statement("id", QueryMethod.In, tests.stream().map(Test::getId).toArray()).build();
        OctaneCollection<EntityModel> automatedTests =
                automatedTestsEntityList.get().addFields("name", "class_name", "package").query(query).execute();
        automatedTests.forEach(automatedTest -> {
            Optional<Test> testFilteredOpt =
                    tests.stream().filter(test -> test.getId().equals(automatedTest.getId())).findFirst();

            if (testFilteredOpt.isPresent()) {
                Test testFiltered = testFilteredOpt.get();
                automatedTest.setValue(new StringFieldModel("name", testFiltered.getName()));
                automatedTest.setValue(new StringFieldModel("class_name", testFiltered.getClassName()));
                automatedTest.setValue(new StringFieldModel("package", testFiltered.getTestPackage()));
            }

        });
        if (!automatedTests.isEmpty()) {

            automatedTestsEntityList.update().entities(automatedTests).execute();

            System.out.println("Tests updated");
        } else {
            System.out.println("No tests found!");
        }
    }


}