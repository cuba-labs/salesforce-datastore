package com.company.sf.core;

import com.company.sf.exception.SalesforceAccessException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that is used for accessing salesforce REST API
 */
@Component
public class SalesforceRestClient {

    @Inject
    private SalesforceConfig salesforceConfig;

    private Logger log = LoggerFactory.getLogger(SalesforceRestClient.class);

    public String executeSOQLQuery(String query) {
        String oauthToken = login();
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(salesforceConfig.getInstanceUrl() + "/services/data/v20.0/query");
            uriBuilder.addParameter("q", query);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", "Bearer " + oauthToken);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                    String errorMessage = "SOQL query execution error";
                    if (httpEntity != null) {
                        String responseMessage = EntityUtils.toString(httpEntity);
                        errorMessage = JsonPath.parse(responseMessage).read("$.[0].message");
                        log.error("SOQL query execution error. Status: {}. Message: {}", response.getStatusLine(), errorMessage);
                    }
                    throw new SalesforceAccessException(errorMessage);
                }
                return EntityUtils.toString(httpEntity);
            }
        } catch (URISyntaxException | IOException e) {
            throw new SalesforceAccessException("SOQL query execution error", e);
        }
    }

    public String loadObjectFields(String salesforceObjectName, String salesforceObjectId, List<String> fieldNames) throws SalesforceAccessException {
        String oauthToken = login();
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(salesforceConfig.getInstanceUrl() + "/services/data/v20.0/sobjects/" + salesforceObjectName + "/" + salesforceObjectId);
            uriBuilder.addParameter("fields", fieldNames.stream().collect(Collectors.joining(",")));
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", "Bearer " + oauthToken);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity httpEntity = response.getEntity();
                if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                    String errorMessage = "Object fields list loading error";
                    if (httpEntity != null) {
                        String responseMessage = EntityUtils.toString(httpEntity);
                        errorMessage = JsonPath.parse(responseMessage).read("$.[0].message");
                        log.error("Object fields list loading error. Status: {}. Message: {}", response.getStatusLine(), errorMessage);
                    }
                    throw new SalesforceAccessException(errorMessage);
                }
                return EntityUtils.toString(httpEntity);
            }
        } catch (URISyntaxException | IOException e) {
            throw new SalesforceAccessException("Object fields list loading error", e);
        }
    }

    public String createNewRecord(String salesforceObjectName, String json) throws SalesforceAccessException {
        String oauthToken = login();
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(salesforceConfig.getInstanceUrl() + "/services/data/v20.0/sobjects/" + salesforceObjectName);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader("Authorization", "Bearer " + oauthToken);
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseHttpEntity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != HttpStatus.SC_CREATED) {
                    String errorMessage = "Object creation error";
                    if (responseHttpEntity != null) {
                        String responseMessage = EntityUtils.toString(responseHttpEntity);
                        errorMessage = JsonPath.parse(responseMessage).read("$.[0].message");
                        log.error("Object creation error. Status: {}. Message: {}", response.getStatusLine(), errorMessage);
                    }
                    throw new SalesforceAccessException(errorMessage);
                }
                String responseJson = EntityUtils.toString(responseHttpEntity);
                DocumentContext ctx = JsonPath.parse(responseJson);
                return ctx.read("$.id");
            }
        } catch (URISyntaxException | IOException e) {
            throw new SalesforceAccessException("Object creation error", e);
        }
    }

    public void updateRecord(String salesforceObjectName, String salesforceObjectId, String json) throws SalesforceAccessException {
        String oauthToken = login();
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(salesforceConfig.getInstanceUrl() + "/services/data/v20.0/sobjects/" + salesforceObjectName + "/" + salesforceObjectId);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPatch httpPatch = new HttpPatch(uriBuilder.build());
            httpPatch.setHeader("Authorization", "Bearer " + oauthToken);
            httpPatch.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                    HttpEntity entity = response.getEntity();
                    String errorMessage = "Object update error";
                    if (entity != null) {
                        String responseMessage = EntityUtils.toString(response.getEntity());
                        errorMessage = JsonPath.parse(responseMessage).read("$.[0].message");
                        log.error("Object update error. Status: {}. Message: {}", response.getStatusLine(), errorMessage);
                    }
                    throw new SalesforceAccessException(errorMessage);
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new SalesforceAccessException("Object update error", e);
        }
    }

    public void deleteRecord(String salesforceObjectName, String salesforceObjectId) throws SalesforceAccessException {
        String oauthToken = login();
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(salesforceConfig.getInstanceUrl() + "/services/data/v20.0/sobjects/" + salesforceObjectName + "/" + salesforceObjectId);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
            httpDelete.setHeader("Authorization", "Bearer " + oauthToken);
            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                    HttpEntity entity = response.getEntity();
                    String errorMessage = "Object deletion error";
                    if (entity != null) {
                        String responseMessage = EntityUtils.toString(response.getEntity());
                        errorMessage = JsonPath.parse(responseMessage).read("$.[0].message");
                        log.error("Object deletion error. Status: {}. Message: {}", response.getStatusLine(), errorMessage);
                    }
                    throw new SalesforceAccessException(errorMessage);
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new SalesforceAccessException("Object deletion error", e);
        }
    }

    /**
     *  Method performs a login to the salesforce REST API using settings stored in the {@link SalesforceConfig}
     *  and returns an OAuth access token
     */
    private String login() {
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("grant_type", "password"));
        formParams.add(new BasicNameValuePair("username", salesforceConfig.getUsername()));
        formParams.add(new BasicNameValuePair("password", salesforceConfig.getPassword()));
        formParams.add(new BasicNameValuePair("client_id", salesforceConfig.getClientId()));
        formParams.add(new BasicNameValuePair("client_secret", salesforceConfig.getClientSecret()));

        UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        String tokenUrl = salesforceConfig.getInstanceUrl() + "/services/oauth2/token";
        HttpPost httpPost = new HttpPost(tokenUrl);
        httpPost.setEntity(requestEntity);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity httpEntity = response.getEntity();
            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                if (httpEntity != null) {
                    log.error("OAuth token error response. Status: {}. Message: {}", statusLine.getStatusCode(), EntityUtils.toString(response.getEntity()));
                }
                throw new SalesforceAccessException("Cannot get salesforce OAuth token");
            }

            String json = EntityUtils.toString(httpEntity);
            DocumentContext ctx = JsonPath.parse(json);
            return ctx.read("$.access_token");
        } catch (IOException e) {
            log.error("Error when getting Salesforce OAuth token", e);
            throw new SalesforceAccessException("Cannot get salesforce OAuth token", e);
        }
    }
}
