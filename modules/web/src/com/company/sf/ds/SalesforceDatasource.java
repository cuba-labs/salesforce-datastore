package com.company.sf.ds;

import com.company.sf.core.SalesforceMetadata;
import com.company.sf.entity.SalesforceEntity;
import com.company.sf.exception.SalesforceAccessException;
import com.google.common.base.Strings;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
public class SalesforceDatasource extends CustomCollectionDatasource<SalesforceEntity, String> {

    private DataManager dataManager = AppBeans.get(DataManager.class);

    private SalesforceMetadata salesforceMetadata = AppBeans.get(SalesforceMetadata.class);

    @Override
    protected Collection<SalesforceEntity> getEntities(Map<String, Object> params) {
        if (Strings.isNullOrEmpty(query)) {
            query = createDefaultSOQLQuery();
        }
        LoadContext ctx = LoadContext.create(getMetaClass().getJavaClass())
                .setQuery(LoadContext.createQuery(query));
        return dataManager.loadList(ctx);
    }

    private String createDefaultSOQLQuery() {
        String salesforceObjectName = salesforceMetadata.getSalesforceObjectName(metaClass);
        if (Strings.isNullOrEmpty(salesforceObjectName)) {
            throw new SalesforceAccessException(String.format("SalesforceObject annotation for class %s is not defined", metaClass.getJavaClass()));
        }

        List<String> salesforceFields = new ArrayList<>();
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            String salesforceFieldName = salesforceMetadata.getSalesforceFieldName(metaProperty);
            if (!Strings.isNullOrEmpty(salesforceFieldName)) {
                salesforceFields.add(salesforceFieldName);
            }
        }

        if (salesforceFields.isEmpty()) {
            throw new SalesforceAccessException("No salesforce fields found for the query");
        }

        return "select " +
                salesforceFields.stream().collect(Collectors.joining(", ")) +
                " from " +
                salesforceObjectName;
    }

}
