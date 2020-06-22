package com.implemica;

import com.google.actions.api.*;
import com.google.actions.api.response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Implements all intent handlers for this Action. Note that your App must extend from DialogflowApp
 * if using Dialogflow or ActionsSdkApp for ActionsSDK based Actions.
 */
public class CalculateApp extends DialogflowApp {

   private static final Logger LOGGER = LoggerFactory.getLogger(CalculateApp.class);

   @ForIntent("Sum Intent")
   public ActionResponse  sum(ActionRequest request) {
      LOGGER.info("Sum Intent");
      ResponseBuilder responseBuilder = getResponseBuilder(request);

      double number1 = (double) request.getParameter("number1");
      double number2 = (double) request.getParameter("number2");

      responseBuilder.add("Sum is " + (number1 + number2));

      return responseBuilder.build();
   }

   @ForIntent("Minus Intent")
   public ActionResponse minus(ActionRequest request) {
      LOGGER.info("Minus Intent");
      ResponseBuilder responseBuilder = getResponseBuilder(request);

      double number1 = (double) request.getParameter("number1");
      double number2 = (double) request.getParameter("number2");

      responseBuilder.add("Sum is " + (number1 - number2));

      return responseBuilder.build();
   }
}
