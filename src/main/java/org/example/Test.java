package org.example;

public class Test {
    private String id;
    private String name;
    private String className;
    private String testPackage;

    public Test(String id, String name, String className, String testPackage) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.testPackage = testPackage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTestPackage() {
        return testPackage;
    }

    public void setTestPackage(String testPackage) {
        this.testPackage = testPackage;
    }
}
