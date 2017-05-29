package com.company.sf.web.account;

import com.company.sf.ds.SalesforceDatasource;
import com.haulmont.cuba.gui.components.AbstractLookup;

import javax.inject.Inject;

public class AccountBrowse extends AbstractLookup {

    @Inject
    private SalesforceDatasource accountsDs;

    @Override
    public void ready() {
        super.ready();
        accountsDs.refresh();
    }
}