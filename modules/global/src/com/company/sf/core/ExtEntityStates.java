package com.company.sf.core;

import com.company.sf.entity.SalesforceEntity;
import com.company.sf.entity.SalesforceEntityInternalAccess;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.EntityStates;

/**
 * Overriden EntityStates bean that can work with SalesforceEntity
 */
public class ExtEntityStates extends EntityStates {

    @Override
    public boolean isNew(Object entity) {
        Preconditions.checkNotNullArgument(entity, "entity is null");
        if (entity instanceof SalesforceEntity) {
            return SalesforceEntityInternalAccess.isNew((SalesforceEntity) entity);
        }
        return super.isNew(entity);
    }
}
