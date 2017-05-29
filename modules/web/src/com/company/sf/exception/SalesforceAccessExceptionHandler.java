package com.company.sf.exception;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.exception.AbstractGenericExceptionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 */
@Component("sf_SalesforceAccessExceptionHandler")
public class SalesforceAccessExceptionHandler extends AbstractGenericExceptionHandler {

    public SalesforceAccessExceptionHandler() {
        super(SalesforceAccessException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(message, Frame.NotificationType.ERROR);
    }
}
