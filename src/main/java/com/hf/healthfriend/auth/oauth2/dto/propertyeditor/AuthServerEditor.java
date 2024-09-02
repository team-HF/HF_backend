package com.hf.healthfriend.auth.oauth2.dto.propertyeditor;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;

import java.beans.PropertyEditorSupport;

public class AuthServerEditor extends PropertyEditorSupport {
    private static final AuthServerEditor singletonInstance = new AuthServerEditor();

    public static AuthServerEditor getInstance() {
        return singletonInstance;
    }

    @Override
    public String getAsText() {
        return ((AuthServer)super.getValue()).name();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setValue(AuthServer.valueOf(text));
    }
}
