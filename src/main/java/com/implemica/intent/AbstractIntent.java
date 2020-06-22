package com.implemica.intent;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.response.ResponseBuilder;
import com.google.api.services.dialogflow_fulfillment.v2.model.WebhookRequest;

import java.io.IOException;
import java.util.ResourceBundle;

public abstract class AbstractIntent extends DialogflowApp {
    public ActionRequest inputRequest;
    public ResponseBuilder builder;
    public String surface = "";

    ResourceBundle bundle;
    public AbstractIntent(String request) {
        inputRequest = createRequest(request, null);
        builder = getResponseBuilder(inputRequest);

        WebhookRequest appRequest = inputRequest.getWebhookRequest();
        if (appRequest != null) {
            surface = appRequest.getOriginalDetectIntentRequest().getSource();
        }
    }

    public abstract String execute() throws IOException;
}
