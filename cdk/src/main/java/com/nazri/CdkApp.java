package com.nazri;

import com.nazri.config.StackConfig;
import com.nazri.stack.APIGatewayStack;
import com.nazri.stack.APIStack;
import com.nazri.util.Constant;
import software.amazon.awscdk.App;

public class CdkApp {
    public static void main(final String[] args) {
        App app = new App();

        // Get environment from context or default to dev
        String environment = (String) app.getNode().tryGetContext("env");

        if (environment.equalsIgnoreCase(Constant.PRD)) {
            environment = Constant.PRD;
        } else {
            environment = Constant.DEV;
        }

        StackConfig stackConfig = getStackConfig(environment);

        APIGatewayStack apiGatewayStack = new APIGatewayStack(app, "warranty-tracker-api-gw-stack-" + environment, stackConfig.getStackProps()
                .stackName("warranty-tracker-api-gw-stack-" + environment)
                .description("Warranty Tracker HTTP API Gateway Stack: " + environment)
                .build(),
                stackConfig);

        APIStack apiStack = new APIStack(app, "warranty-tracker-api-stack-" + environment, stackConfig.getStackProps()
                .stackName("warranty-tracker-api-stack-" + environment)
                .description("Warranty Tracker API Stack " + environment)
                .build(),
                stackConfig,
                apiGatewayStack.getHttpApi()
        );

        app.synth();
    }

    public static StackConfig getStackConfig(String environment) {
        StackConfig stackConfig;
        switch (environment) {
            case Constant.PRD:
                stackConfig = new StackConfig.Builder()
                        .withEnvironment(Constant.PRD)
                        .build();
                break;
            default:
                stackConfig = new StackConfig.Builder()
                        .withEnvironment(Constant.DEV)
                        .build();
        }
        return stackConfig;
    }
}

