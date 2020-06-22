package com.implemica.intent;

/**
 * @author Pc created on 6/5/2020 inside the package - com.implemica.intent
 */
public class MinusIntent extends AbstractIntent{
   @Intent("Minus Intent")
   public MinusIntent(String request) {
      super(request);
   }

   @Override
   public String execute() {
      builder.add(minus());

      return builder.build().toJson();
   }

   private String minus(){
      Double number1 = (Double) inputRequest.getParameter("number1");
      Double number2 = (Double) inputRequest.getParameter("number2");
      String sum = "can't be calculated.";
      if (number1 != null && number2 != null) {
         sum = "is " + (number1 - number2);
      }
      return "Difference " + sum;
   }
}
