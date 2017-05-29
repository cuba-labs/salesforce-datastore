package com.company.sf;

import com.company.sf.core.SalesforceSerializer;
import com.company.sf.entity.Account;
import com.haulmont.cuba.core.global.AppBeans;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 */
public class SalesforceSerializationTest {

    @ClassRule
    public static SalesforceTestContainer cont = SalesforceTestContainer.Common.INSTANCE;

    private SalesforceSerializer salesforceSerializer;

    @Before
    public void before() {
        salesforceSerializer = AppBeans.get(SalesforceSerializer.class);
    }

    @Test
    public void testSerialization() {
        Account account = cont.metadata().create(Account.class);
        account.setId("123");
        account.setName("Test account");
        String json = salesforceSerializer.entityToJson(account);
        System.out.println(json);
    }

    @Test
    public void testDeserialization() {
        String json = "{\"attributes\":{\"type\":\"Account\",\"url\":\"/services/data/v20.0/sobjects/Account/0010Y00000Q3mkbQAB\"},\"Name\":\"TestAccount1-1\",\"Id\":\"0010Y00000Q3mkbQAB\"}";
        Account account = salesforceSerializer.entityFromJson(json, cont.metadata().getClassNN(Account.class));
        System.out.println(account.getId());
        System.out.println(account.getName());
    }
}
