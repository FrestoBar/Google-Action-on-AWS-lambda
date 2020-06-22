package com.implemica.intent;

public class SumIntent extends AbstractIntent {
    @Intent("Sum Intent")
    public SumIntent(String request) {
        super(request);
    }

    @Override
    public String execute() {
        builder.add(sum());

        return builder.build().toJson();
    }

    private String sum(){
        Double number1 = (Double) inputRequest.getParameter("number1");
        Double number2 = (Double) inputRequest.getParameter("number2");
        String sum = "can't be calculated.";
        if (number1 != null && number2 != null) {
            sum = "is " + (number1 + number2);
        }
        return "Sum " + sum;
    }
}
