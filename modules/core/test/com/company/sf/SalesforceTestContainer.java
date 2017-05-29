package com.company.sf;

import com.haulmont.cuba.testsupport.TestContainer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 */
public class SalesforceTestContainer extends TestContainer {

    public SalesforceTestContainer() {
        super();
        appComponents = new ArrayList<>(Arrays.asList(
                "com.haulmont.cuba"
        ));
        appPropertiesFiles = Arrays.asList(
                // List the files defined in your web.xml
                // in appPropertiesConfig context parameter of the core module
                "cuba-app.properties",
                "/com/company/sf/app.properties",
                "test-app.properties",
                "/com/company/sf/sf-test-app.properties");
        dbDriver = "org.hsqldb.jdbc.JDBCDriver";
        dbUrl = "jdbc:hsqldb:hsql://localhost/sf";
        dbUser = "sa";
        dbPassword = "";
    }

    public static class Common extends SalesforceTestContainer {

        public static final SalesforceTestContainer.Common INSTANCE = new SalesforceTestContainer.Common();

        private static volatile boolean initialized;

        private Common() {
        }

        @Override
        public void before() throws Throwable {
            if (!initialized) {
                super.before();
                initialized = true;
            }
            setupContext();
        }

        @Override
        public void after() {
            cleanupContext();
            // never stops - do not call super
        }
    }
}
