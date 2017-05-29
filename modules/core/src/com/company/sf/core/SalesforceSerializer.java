package com.company.sf.core;

import com.company.sf.entity.SalesforceEntity;
import com.company.sf.exception.SalesforceAccessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.impl.*;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Class is responsible for transforming salesforce JSON to CUBA entities and vice versa
 */
@Component
public class SalesforceSerializer {

    @Inject
    private Metadata metadata;

    @Inject
    private SalesforceMetadata salesforceMetadata;

    private Logger log = LoggerFactory.getLogger(SalesforceSerializer.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public <E extends Entity> List<E> entitiesListFromJson(String json, MetaClass metaClass) {
        List<E> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ObjectNode rootNode = (ObjectNode) objectMapper.readTree(json);
            ArrayNode records = (ArrayNode) rootNode.get("records");
            for (JsonNode recordNode : records) {
                E entity = _entityFromJson(recordNode, metaClass);
                result.add(entity);
            }
        } catch (IOException e) {
            log.error("Cannot parse salesforce JSON", e);
            throw new SalesforceAccessException("Cannot parse salesforce JSON");
        }
        return result;
    }

    public <E extends Entity> E entityFromJson(String json, MetaClass metaClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ObjectNode rootNode = (ObjectNode) objectMapper.readTree(json);
            return _entityFromJson(rootNode, metaClass);
        } catch (IOException e) {
            log.error("Cannot parse salesforce JSON", e);
            throw new SalesforceAccessException("Cannot parse salesforce JSON");
        }
    }

    private <E extends Entity> E _entityFromJson(JsonNode recordNode, MetaClass metaClass) {
        E entity = (E) metadata.create(metaClass);
        for (Iterator<String> it = recordNode.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
            MetaProperty metaProperty = findMetaPropertyBySalesforceFieldName(metaClass, fieldName);
            if (metaProperty != null) {
                JsonNode fieldNode = recordNode.get(fieldName);
                String jsonFieldValue = fieldNode.isNull() ? null : fieldNode.asText();
                if (jsonFieldValue == null) continue;
                Object entityFieldValue = parseJsonFieldValue(metaProperty, jsonFieldValue);
                entity.setValue(metaProperty.getName(), entityFieldValue);
            }

        }
        return entity;
    }

    private Object parseJsonFieldValue(MetaProperty metaProperty, String jsonFieldValue) {
        Object entityFieldValue = null;
        Datatype<Object> datatype = metaProperty.getRange().asDatatype();
        try {
            switch (datatype.getName()) {
                case DateDatatype.NAME:
                    entityFieldValue = dateFormat.parse(jsonFieldValue);
                    break;
                case DateTimeDatatype.NAME:
                    entityFieldValue = dateTimeFormat.parse(jsonFieldValue);
                    break;
                case BooleanDatatype.NAME:
                case IntegerDatatype.NAME:
                case LongDatatype.NAME:
                case BigDecimalDatatype.NAME:
                case DoubleDatatype.NAME:
                case StringDatatype.NAME:
                    entityFieldValue = datatype.parse(jsonFieldValue);
                    break;
                default:
                    log.warn("datatype {} is not supported", datatype.getName());
            }
        } catch (ParseException e) {
            log.warn("Error on parsing the field value", e);
        }
        return entityFieldValue;
    }

    public String entityToJson(SalesforceEntity entity) {
        MetaClass metaClass = entity.getMetaClass();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            if ("id".equals(metaProperty.getName())) continue;
            if (!metaProperty.getRange().isDatatype()) continue;
            String salesforceField = salesforceMetadata.getSalesforceFieldName(metaProperty);
            if (Strings.isNullOrEmpty(salesforceField)) continue;

            Object propertyValue = entity.getValue(metaProperty.getName());
            if (propertyValue == null) {
                objectNode.putNull(salesforceField);
                continue;
            }
            Datatype<Object> datatype = metaProperty.getRange().asDatatype();
            switch (datatype.getName()) {
                case DateDatatype.NAME:
                    String jsonPropertyValue = dateFormat.format((Date) propertyValue);
                    objectNode.put(salesforceField, jsonPropertyValue);
                    break;
                case DateTimeDatatype.NAME:
                    jsonPropertyValue = dateTimeFormat.format((Date) propertyValue);
                    objectNode.put(salesforceField, jsonPropertyValue);
                    break;
                case BooleanDatatype.NAME:
                    objectNode.put(salesforceField, (boolean) propertyValue);
                    break;
                case IntegerDatatype.NAME:
                    objectNode.put(salesforceField, (int) propertyValue);
                    break;
                case LongDatatype.NAME:
                    objectNode.put(salesforceField, (long) propertyValue);
                    break;
                case BigDecimalDatatype.NAME:
                    objectNode.put(salesforceField, (BigDecimal) propertyValue);
                    break;
                case DoubleDatatype.NAME:
                    objectNode.put(salesforceField, (double) propertyValue);
                    break;
                case StringDatatype.NAME:
                    objectNode.put(salesforceField, (String) propertyValue);
                    break;
                default:
                    log.warn("datatype {} is not supported", datatype.getName());
            }
        }
        return objectNode.toString();
    }

    @Nullable
    private MetaProperty findMetaPropertyBySalesforceFieldName(MetaClass metaClass, String salesforceFieldName) {
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            String salesforceField = salesforceMetadata.getSalesforceFieldName(metaProperty);
            if (salesforceFieldName.equals(salesforceField)) return metaProperty;
        }
        return null;
    }

}
