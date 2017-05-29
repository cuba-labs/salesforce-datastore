package com.company.sf.core;

import com.company.sf.annotation.SalesforceField;
import com.company.sf.annotation.SalesforceObject;
import com.google.common.base.Strings;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

/**
 * Class is used for accessing salesforce metadata information that is stored in entity annotations
 */
@Component
public class SalesforceMetadata {

    @Inject
    private MetadataTools metadataTools;

    @Nullable
    public String getSalesforceObjectName(MetaClass metaClass) {
        return (String) metadataTools.getMetaAnnotationAttributes(metaClass.getAnnotations(), SalesforceObject.class).get("value");
    }

    @Nullable
    public String getSalesforceFieldName(MetaProperty metaProperty) {
        return (String) metadataTools.getMetaAnnotationAttributes(metaProperty.getAnnotations(), SalesforceField.class).get("value");
    }

    public List<String> getSalesforceFieldNames(MetaClass metaClass) {
        List<String> results = new ArrayList<>();
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            String salesforceFieldName = getSalesforceFieldName(metaProperty);
            if (!Strings.isNullOrEmpty(salesforceFieldName)) results.add(salesforceFieldName);
        }
        return results;
    }
}
