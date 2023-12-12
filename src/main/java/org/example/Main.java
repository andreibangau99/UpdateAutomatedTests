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

import java.util.Collections;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        Authentication clientAuthentication = new SimpleClientAuthentication("sa@nga", "Welcome1");
        final Octane.Builder octaneBuilder = new Octane.Builder(clientAuthentication);

        octaneBuilder.Server("http://localhost:8080/dev");
        octaneBuilder.sharedSpace(1001);
        octaneBuilder.workSpace(6001);

        Octane octane = octaneBuilder.build();


        EntityList automatedTestsEntityList = octane.entityList("automated_tests");
        String testId = args[0];
        String testName = args[1];
        String className = args[2];
        String testPackage = args[3];
        updateTestById(automatedTestsEntityList, testId, testName,className, testPackage);
    }

    private static void updateTestById(EntityList automatedTestsEntityList, String testId, String testName,String className, String testPackage) {
        final Query query = Query.statement("id", QueryMethod.EqualTo, testId).build();
        OctaneCollection<EntityModel> automatedTests =
                automatedTestsEntityList.get().addFields("name", "class_name", "package").query(query).execute();
        Optional<EntityModel> automatedTestOpt = automatedTests.stream().findFirst();
        if (automatedTestOpt.isPresent()) {
            EntityModel automatedTest = automatedTestOpt.get();
            automatedTest.setValue(new StringFieldModel("name", testName));
            automatedTest.setValue(new StringFieldModel("class_name", className));
            automatedTest.setValue(new StringFieldModel("package", testPackage));
            automatedTestsEntityList.update().entities(Collections.singleton(automatedTest)).execute();
            System.out.println("Test updated");
        } else {
            System.out.println("Test not found!");
        }
    }


}