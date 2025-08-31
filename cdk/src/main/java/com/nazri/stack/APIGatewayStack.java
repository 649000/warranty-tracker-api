package com.nazri.stack;

import com.nazri.config.StackConfig;
import com.nazri.util.Constant;
import com.nazri.util.TagUtil;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpNoneAuthorizer;
import software.constructs.Construct;

public class APIGatewayStack extends Stack {
    private final StackConfig stackConfig;
    private final HttpApi httpApi;

    public APIGatewayStack(final Construct scope, final String id, final StackProps props, StackConfig stackConfig) {
        super(scope, id, props);
        this.stackConfig = stackConfig;
        this.httpApi = createHTTPAPIGateway();
        TagUtil.addTags(this.httpApi, stackConfig);
    }

    private HttpApi createHTTPAPIGateway() {
        final String environment = stackConfig.getTags().get(Constant.ENVIRONMENT);
        return HttpApi.Builder.create(this, "warranty-tracker-api-gateway")
                .apiName("Warranty Tracker HTTP API Gateway - " + environment)
                .description("Warranty Tracker: HTTP API Gateway: " + stackConfig.getTags().get(Constant.ENVIRONMENT))
                .defaultAuthorizer(new HttpNoneAuthorizer())
                .build();
    }

    public HttpApi getHttpApi() {
        return httpApi;
    }
}
