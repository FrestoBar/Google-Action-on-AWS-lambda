package com.implemica;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.implemica.intent.AbstractIntent;
import com.implemica.intent.Intent;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import lombok.SneakyThrows;
import org.apache.logging.log4j.core.util.IOUtils;
import org.reflections.Reflections;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

/**
 * Handles request received via AWS - API Gateway [proxy integration](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html)
 * and delegates to your Actions App.
 */
public class AWSLambdaHandler implements RequestStreamHandler {
//   private final App actionsApp = new CalculateApp();
//   private final JSONParser parser = new JSONParser();

   /**
    * Find class that response for input command.
    *
    * @param command Input command
    * @return Command constructor.
    * @throws NoSuchMethodException Thrown when a particular method cannot be found.
    */
   private static Constructor init(String command) throws NoSuchMethodException {
      Reflections.log = null;//todo comment
      Reflections reflections = new Reflections("com.implemica");

      Set<Class<? extends AbstractIntent>> allClasses = reflections.getSubTypesOf(AbstractIntent.class);
      for (Class<?> allClass : allClasses) {
         Set<Constructor> injectables = Collections.singleton(allClass.getDeclaredConstructor(String.class));

         for (Constructor m : injectables) {
            if (m.isAnnotationPresent(Intent.class)) {
               Intent cmd = (Intent) m.getAnnotation(Intent.class);
               if (cmd.value().equals(command)) {
                  return m;
               }
            }
         }
      }
      return null;
   }

  @SneakyThrows
  @Override
  public void handleRequest(InputStream inputStream,
                            OutputStream outputStream,
                            Context context) {
     Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider()).build();

     String request = IOUtils.toString(new InputStreamReader(inputStream));
     String intent = JsonPath.using(conf).parse(request).read("$.queryResult.intent.displayName").toString();
     intent = intent.substring(1, intent.length() - 1);

     Constructor constructor = init(intent);
     AbstractIntent command = (AbstractIntent) (constructor.newInstance(request));
     outputStream.write(command.execute().getBytes());
  }


  public static final String testExample = "{\n" +
                       "    \"originalDetectIntentRequest\": {\n" +
                       "        \"payload\": {\n" +
                       "            \"isInSandbox\": true,\n" +
                       "            \"surface\": {\n" +
                       "                \"capabilities\": [\n" +
                       "                    {\n" +
                       "                        \"name\": \"actions.capability.AUDIO_OUTPUT\"\n" +
                       "                    },\n" +
                       "                    {\n" +
                       "                        \"name\": \"actions.capability.SCREEN_OUTPUT\"\n" +
                       "                    },\n" +
                       "                    {\n" +
                       "                        \"name\": \"actions.capability.ACCOUNT_LINKING\"\n" +
                       "                    },\n" +
                       "                    {\n" +
                       "                        \"name\": \"actions.capability.MEDIA_RESPONSE_AUDIO\"\n" +
                       "                    }\n" +
                       "                ]\n" +
                       "            },\n" +
                       "            \"requestType\": \"SIMULATOR\",\n" +
                       "            \"inputs\": [\n" +
                       "                {\n" +
                       "                    \"rawInputs\": [\n" +
                       "                        {\n" +
                       "                            \"query\": \"5 + 12\",\n" +
                       "                            \"inputType\": \"VOICE\"\n" +
                       "                        }\n" +
                       "                    ],\n" +
                       "                    \"arguments\": [\n" +
                       "                        {\n" +
                       "                            \"rawText\": \"5 + 12\",\n" +
                       "                            \"textValue\": \"5 + 12\",\n" +
                       "                            \"name\": \"text\"\n" +
                       "                        }\n" +
                       "                    ],\n" +
                       "                    \"intent\": \"actions.intent.TEXT\"\n" +
                       "                }\n" +
                       "            ],\n" +
                       "            \"user\": {\n" +
                       "                \"userVerificationStatus\": \"VERIFIED\",\n" +
                       "                \"lastSeen\": \"2020-06-05T04:48:46Z\",\n" +
                       "                \"locale\": \"en-US\"\n" +
                       "            },\n" +
                       "            \"conversation\": {\n" +
                       "                \"conversationId\": \"ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw\",\n" +
                       "                \"type\": \"ACTIVE\",\n" +
                       "                \"conversationToken\": \"[]\"\n" +
                       "            },\n" +
                       "            \"availableSurfaces\": [\n" +
                       "                {\n" +
                       "                    \"capabilities\": [\n" +
                       "                        {\n" +
                       "                            \"name\": \"actions.capability.WEB_BROWSER\"\n" +
                       "                        },\n" +
                       "                        {\n" +
                       "                            \"name\": \"actions.capability.SCREEN_OUTPUT\"\n" +
                       "                        },\n" +
                       "                        {\n" +
                       "                            \"name\": \"actions.capability.AUDIO_OUTPUT\"\n" +
                       "                        }\n" +
                       "                    ]\n" +
                       "                }\n" +
                       "            ]\n" +
                       "        },\n" +
                       "        \"source\": \"google\",\n" +
                       "        \"version\": \"2\"\n" +
                       "    },\n" +
                       "    \"session\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw\",\n" +
                       "    \"queryResult\": {\n" +
                       "        \"allRequiredParamsPresent\": true,\n" +
                       "        \"fulfillmentMessages\": [\n" +
                       "            {\n" +
                       "                \"text\": {\n" +
                       "                    \"text\": [\n" +
                       "                        \"\"\n" +
                       "                    ]\n" +
                       "                }\n" +
                       "            }\n" +
                       "        ],\n" +
                       "        \"outputContexts\": [\n" +
                       "            {\n" +
                       "                \"name\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw/contexts/actions_capability_audio_output\",\n" +
                       "                \"parameters\": {\n" +
                       "                    \"number1.original\": \"5\",\n" +
                       "                    \"sum-operations\": \"+\",\n" +
                       "                    \"number2.original\": \"12\",\n" +
                       "                    \"number1\": 5,\n" +
                       "                    \"number2\": 12,\n" +
                       "                    \"sum-operations.original\": \"+\"\n" +
                       "                }\n" +
                       "            },\n" +
                       "            {\n" +
                       "                \"name\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw/contexts/actions_capability_screen_output\",\n" +
                       "                \"parameters\": {\n" +
                       "                    \"number1.original\": \"5\",\n" +
                       "                    \"sum-operations\": \"+\",\n" +
                       "                    \"number2.original\": \"12\",\n" +
                       "                    \"number1\": 5,\n" +
                       "                    \"number2\": 12,\n" +
                       "                    \"sum-operations.original\": \"+\"\n" +
                       "                }\n" +
                       "            },\n" +
                       "            {\n" +
                       "                \"name\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw/contexts/actions_capability_account_linking\",\n" +
                       "                \"parameters\": {\n" +
                       "                    \"number1.original\": \"5\",\n" +
                       "                    \"sum-operations\": \"+\",\n" +
                       "                    \"number2.original\": \"12\",\n" +
                       "                    \"number1\": 5,\n" +
                       "                    \"number2\": 12,\n" +
                       "                    \"sum-operations.original\": \"+\"\n" +
                       "                }\n" +
                       "            },\n" +
                       "            {\n" +
                       "                \"name\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw/contexts/actions_capability_media_response_audio\",\n" +
                       "                \"parameters\": {\n" +
                       "                    \"number1.original\": \"5\",\n" +
                       "                    \"sum-operations\": \"+\",\n" +
                       "                    \"number2.original\": \"12\",\n" +
                       "                    \"number1\": 5,\n" +
                       "                    \"number2\": 12,\n" +
                       "                    \"sum-operations.original\": \"+\"\n" +
                       "                }\n" +
                       "            },\n" +
                       "            {\n" +
                       "                \"name\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw/contexts/google_assistant_input_type_voice\",\n" +
                       "                \"parameters\": {\n" +
                       "                    \"number1.original\": \"5\",\n" +
                       "                    \"sum-operations\": \"+\",\n" +
                       "                    \"number2.original\": \"12\",\n" +
                       "                    \"number1\": 5,\n" +
                       "                    \"number2\": 12,\n" +
                       "                    \"sum-operations.original\": \"+\"\n" +
                       "                }\n" +
                       "            },\n" +
                       "            {\n" +
                       "                \"name\": \"projects/newagent-ogql/agent/sessions/ABwppHEy4C-W-15NQh-Ao7gOSP-m2pAdJaB8VQZ-kX2TB-KgU4yP6t6A66DakfFPZTjqs6AIL_PbAF7MVw/contexts/__system_counters__\",\n" +
                       "                \"parameters\": {\n" +
                       "                    \"number1.original\": \"5\",\n" +
                       "                    \"no-input\": 0,\n" +
                       "                    \"sum-operations\": \"+\",\n" +
                       "                    \"no-match\": 0,\n" +
                       "                    \"number2.original\": \"12\",\n" +
                       "                    \"number1\": 5,\n" +
                       "                    \"number2\": 12,\n" +
                       "                    \"sum-operations.original\": \"+\"\n" +
                       "                }\n" +
                       "            }\n" +
                       "        ],\n" +
                       "        \"queryText\": \"5 + 12\",\n" +
                       "        \"languageCode\": \"en\",\n" +
                       "        \"parameters\": {\n" +
                       "            \"sum-operations\": \"+\",\n" +
                       "            \"number1\": 5,\n" +
                       "            \"number2\": 12\n" +
                       "        },\n" +
                       "        \"intent\": {\n" +
                       "            \"displayName\": \"Minus Intent\",\n" +
                       "            \"name\": \"projects/newagent-ogql/agent/intents/6e5274ed-14b5-4bc2-a86e-1fa06fca91dd\"\n" +
                       "        },\n" +
                       "        \"intentDetectionConfidence\": 1\n" +
                       "    },\n" +
                       "    \"responseId\": \"19676b53-6b1a-4f30-9326-0526e5650f8e-b4a98e7e\"\n" +
                       "}";

   @SneakyThrows
   /**
    * Need for tests.
    */
   public static void main(String[] args) {
      Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider()).build();

      String intent = JsonPath.using(conf).parse(testExample).read("$.queryResult.intent.displayName").toString();
      intent = intent.substring(1, intent.length() - 1);

      Constructor constructor = init(intent);
      AbstractIntent command = (AbstractIntent) (constructor.newInstance(testExample));
      System.out.println(command.execute().getBytes());
   }
}
