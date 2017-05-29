package com.company.sf;

import com.company.sf.core.SalesforceRestClient;
import com.company.sf.exception.SalesforceAccessException;
import com.haulmont.cuba.core.global.AppBeans;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Arrays;

/**
 */
public class SalesforceRestClientTest {

    @ClassRule
    public static SalesforceTestContainer cont = SalesforceTestContainer.Common.INSTANCE;

    private SalesforceRestClient salesforceRestClient;

    @Before
    public void before() {
        salesforceRestClient = AppBeans.get(SalesforceRestClient.class);
    }

    @Test
    public void executeSOQL() throws SalesforceAccessException {
        String result = salesforceRestClient.executeSOQLQuery("select id, name, LastModifiedDate from Contact");
        System.out.println(result);
    }

    @Test
    public void createNewAccount() throws SalesforceAccessException {
        String result = salesforceRestClient.createNewRecord("Account", "{\"Name\": \"TestAccount1\"}");
        System.out.println(result);
    }

    @Test
    public void updateAccount() throws SalesforceAccessException {
        salesforceRestClient.updateRecord("Account", "0010Y00000Q3mkbQAB", "{\"Name\": \"TestAccount1-1\"}");
    }

    @Test
    public void loadObjectFields() throws SalesforceAccessException {
        String result = salesforceRestClient.loadObjectFields("Account", "0010Y00000Q3mkbQAB", Arrays.asList(new String[]{"Name", "Id"}));
        System.out.println(result);
    }
}
