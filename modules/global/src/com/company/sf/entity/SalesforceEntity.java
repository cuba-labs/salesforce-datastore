package com.company.sf.entity;

import com.company.sf.annotation.SalesforceField;
import com.google.common.base.Strings;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.impl.AbstractInstance;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.sys.CubaEnhancingDisabled;

import javax.persistence.Id;
import java.util.UUID;

/**
 * Parent class for all Salesforce entities
 */
@com.haulmont.chile.core.annotations.MetaClass(name = "sf$SalesforceEntity")
public abstract class SalesforceEntity extends AbstractInstance
        implements Entity<String>, CubaEnhancingDisabled {

    protected boolean __new = true;

    @MetaProperty
    @SalesforceField("Id")
    @Id
    protected String id;

    protected UUID uuid = UUID.randomUUID();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public MetaClass getMetaClass() {
        Metadata metadata = AppBeans.get(Metadata.NAME);
        return metadata.getSession().getClassNN(getClass());
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null || getClass() != other.getClass())
            return false;

        String otherId = ((SalesforceEntity) other).getId();
        if (!Strings.isNullOrEmpty(getId()) && !Strings.isNullOrEmpty(otherId)) {
            return getId().equals(otherId);
        } else {
            UUID otherUuid = ((SalesforceEntity) other).getUuid();
            return getUuid().equals(otherUuid);
        }
    }
}
