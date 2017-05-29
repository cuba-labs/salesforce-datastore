# Salesforce Datastore

## Overview

This application demonstrates a usage of custom data stores. By default, CUBA Platform application works with RDBMS data store that reads and writes data to relational database. A datastore in this sample gets the data from the Salesforce application using its REST API. As an example, salesforce Account objects are read and edited.
  
## Implementation Details

### Salesforce Connection Configuration

All salesforce connection settings must be defined with the `SalesforceConfig` configuration interface. You can enter the values in the `Administration - Application Properties` screen.

### Account Entity

The `Account` entity is extended from the `SalesforceEntity` class.
 
The `Account` class has a `@SalesforceObject` annotation that specifies the salesforce object name.
  
Fields in `Account` entity are annotates with the `@SalesforceField` annotations that specify salesforce object fields names.
 
### Account Browser Screen

A datasource class for the accounts browser must be `com.company.sf.ds.SalesforceDatasource`.

The datasource query is written using the SOQL (Salesforce Object Query Language) syntax.
 
```xml
<collectionDatasource id="accountsDs"
        datasourceClass="com.company.sf.ds.SalesforceDatasource"
        class="com.company.sf.entity.Account">
    <query>
        <![CDATA[select Id, Name, Phone, Description from Account order by Name]]>
    </query>
</collectionDatasource>
```

### SalesforceDatasource

This `SalesforceDatasource` executes the SOQL query using the `DataManager`. The `Account` entity belongs to the `SalesforceDataStore`, that's why the `DataManager` redirects CRUD operations to this datastore. 

### SalesforceDataStore

`SalesforceDataStore` performs all entity CRUD operations using the salesforce REST API. 

## Known Issues and Limitations

The current version of studio (6.5.1) cannot work with entities that are not descendants of the `BaseGenericIdEntity` or `AbstractNotPersistentEntity` classes. The `Account` class inherits none of them, so it can't be edited in the Studio. In future versions of Studio this limitation will be eliminated.