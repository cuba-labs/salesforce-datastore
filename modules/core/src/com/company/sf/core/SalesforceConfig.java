package com.company.sf.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

/**
 * Configuration interface that defines salesforce connection settings
 */
@Source(type = SourceType.DATABASE)
public interface SalesforceConfig extends Config {

    /**
     * Salesforce username
     */
    @Property("salesforce.username")
    String getUsername();

    /**
     * Salesforce password
     * <p>
     * WARNING! The password here must also contain user's security token:
     * {password}{token}
     */
    @Property("salesforce.password")
    String getPassword();

    /**
     * Salesforce instance URL, e.g. https://eu14.salesforce.com
     */
    @Property("salesforce.instanceUrl")
    String getInstanceUrl();

    /**
     * OAuth2 client key from the salesforce connected app definition
     */
    @Property("salesforce.clientId")
    String getClientId();

    /**
     * OAuth2 client secret from the salesforce connected app definition
     */
    @Property("salesforce.clientSecret")
    String getClientSecret();
}
