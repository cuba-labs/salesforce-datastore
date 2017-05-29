package com.company.sf.core;

import com.company.sf.entity.SalesforceEntity;
import com.company.sf.entity.SalesforceEntityInternalAccess;
import com.google.common.base.Strings;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.app.DataStore;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
@Component(SalesforceDataStore.NAME)
public class SalesforceDataStore implements DataStore {

    public static final String NAME = "sf_SalesforceDataStore";

    @Inject
    private Metadata metadata;

    @Inject
    private SalesforceRestClient salesforceRestClient;

    @Inject
    private SalesforceSerializer salesforceSerializer;

    @Inject
    private SalesforceMetadata salesforceMetadata;

    private Logger log = LoggerFactory.getLogger(SalesforceDataStore.class);

    @Nullable
    @Override
    public <E extends Entity> E load(LoadContext<E> context) {
        MetaClass metaClass = metadata.getClassNN(context.getMetaClass());
        String salesforceObjectName = salesforceMetadata.getSalesforceObjectName(metaClass);
        String json = salesforceRestClient.loadObjectFields(salesforceObjectName,
                (String) context.getId(),
                salesforceMetadata.getSalesforceFieldNames(metaClass));
        E entity = salesforceSerializer.entityFromJson(json, metaClass);
        if (entity != null) {
            SalesforceEntityInternalAccess.setNew((SalesforceEntity) entity, false);
        }
        return entity;
    }

    @Override
    public <E extends Entity> List<E> loadList(LoadContext<E> context) {
        MetaClass metaClass = metadata.getClassNN(context.getMetaClass());
        String queryString = context.getQuery().getQueryString();
        String json = salesforceRestClient.executeSOQLQuery(queryString);
        List<E> resultList = salesforceSerializer.entitiesListFromJson(json, metaClass);
        resultList.forEach(entity -> SalesforceEntityInternalAccess.setNew((SalesforceEntity) entity, false));
        return resultList;
    }

    @Override
    public Set<Entity> commit(CommitContext context) {
        Set<Entity> result = new HashSet<>();
        for (Entity entity : context.getCommitInstances()) {
            if (!(entity instanceof SalesforceEntity)) {
                log.warn("Entity cannot be commited. Only entities that inherit AbstractSalesforceEntity are supported");
                continue;
            }
            String salesforceObjectName = salesforceMetadata.getSalesforceObjectName(entity.getMetaClass());
            if (Strings.isNullOrEmpty(salesforceObjectName)) {
                continue;
            }
            String json = salesforceSerializer.entityToJson((SalesforceEntity) entity);
            if (PersistenceHelper.isNew(entity)) {
                String salesforceObjectId = salesforceRestClient.createNewRecord(salesforceObjectName, json);
                ((SalesforceEntity) entity).setId(salesforceObjectId);
                SalesforceEntityInternalAccess.setNew((SalesforceEntity) entity, false);
            } else {
                salesforceRestClient.updateRecord(salesforceObjectName, ((SalesforceEntity) entity).getId(), json);
            }
            result.add(entity);
        }

        for (Entity entity : context.getRemoveInstances()) {
            String salesforceObjectName = salesforceMetadata.getSalesforceObjectName(entity.getMetaClass());
            salesforceRestClient.deleteRecord(salesforceObjectName, ((SalesforceEntity)entity).getId());
        }

        return result;
    }

    @Override
    public long getCount(LoadContext<? extends Entity> context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<KeyValueEntity> loadValues(ValueLoadContext context) {
        throw new UnsupportedOperationException();
    }
}
