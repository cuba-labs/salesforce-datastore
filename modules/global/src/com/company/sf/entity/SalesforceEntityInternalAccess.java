package com.company.sf.entity;

/**
 */
public class SalesforceEntityInternalAccess {

    public static boolean isNew(SalesforceEntity entity) {
        return entity.__new;
    }

    public static void setNew(SalesforceEntity entity, boolean _new) {
        entity.__new = _new;
    }
}
