package com.company.sf.entity;

import com.company.sf.annotation.SalesforceField;
import com.company.sf.annotation.SalesforceObject;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@NamePattern("%s|name")
@MetaClass(name = "sf$Account")
@SalesforceObject("Account")
public class Account extends SalesforceEntity {
    private static final long serialVersionUID = 3688246653438098520L;

    @MetaProperty
    @SalesforceField("Name")
    protected String name;

    @MetaProperty
    @SalesforceField("Phone")
    protected String phone;

    @MetaProperty
    @SalesforceField("Description")
    protected String description;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}