package com.nazri.stack;

import com.nazri.config.StackConfig;
import com.nazri.util.Constant;
import com.nazri.util.TagUtil;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.aws_apigatewayv2_integrations.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.List;

public class APIStack extends Stack {

    private final Function apiFunction;

    public APIStack(final Construct scope, final String id, final StackProps props, StackConfig stackConfig, HttpApi httpApi) {
        super(scope, id, props);
        final String environment = stackConfig.getTags().get(Constant.ENVIRONMENT);

        HashMap<String, String> lambdaEnvConfig = buildLambdaEnvConfig(environment, stackConfig.getGraalvm());

        if (stackConfig.getGraalvm()) {
            this.apiFunction = createNativeApiFunction(environment, lambdaEnvConfig);
        } else {
            this.apiFunction = createJVMApiFunction(environment, lambdaEnvConfig);
        }

        addHealthRoute(httpApi);
        addUserRoute(httpApi);
        addCompanyRoute(httpApi);
        addProductRoute(httpApi);
        addUserProductRoute(httpApi);
        addWarrantyRoute(httpApi);
        addClaimRoute(httpApi);
        TagUtil.addTags(this.apiFunction, stackConfig);
    }

    private HashMap<String, String> buildLambdaEnvConfig(String environment, boolean isGraalVM) {
        HashMap<String, String> lambdaEnvConfig = new HashMap<>();

        lambdaEnvConfig.put("quarkus_lambda_handler", "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest");

        switch (environment.toLowerCase()) {
            case Constant.DEV:
                lambdaEnvConfig.put("quarkus_datasource_jdbc_url", Constant.DEV_DB_URL);
                lambdaEnvConfig.put("quarkus_datasource_username", Constant.DEV_DB_USERNAME);
                lambdaEnvConfig.put("quarkus_datasource_password", Constant.DEV_DB_PASSWORD);
                break;
            case Constant.PRD:
                lambdaEnvConfig.put("quarkus_datasource_jdbc_url", Constant.PROD_DB_URL);
                lambdaEnvConfig.put("quarkus_datasource_username", Constant.PROD_DB_USERNAME);
                lambdaEnvConfig.put("quarkus_datasource_password", Constant.PROD_DB_PASSWORD);
                break;
            default:
                throw new IllegalArgumentException("Unsupported environment: " + environment);
        }

        if (isGraalVM) {
            lambdaEnvConfig.put("DISABLE_SIGNAL_HANDLER", "true");
        } else {
            lambdaEnvConfig.put("JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
        }

        return lambdaEnvConfig;
    }

    private Function createJVMApiFunction(final String environment, HashMap<String, String> lambdaEnvConfig) {
        return Function.Builder.create(this, "warranty-tracker-api")
                .functionName("warranty-tracker-api-" + environment)
                .description("Warranty Tracker: JVM REST API: " + environment)
                .code(Code.fromAsset("../api/target/function.zip"))
                .timeout(Duration.seconds(15))
                .memorySize(512)
                .runtime(Runtime.JAVA_21)
                .handler("io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest")
                .environment(lambdaEnvConfig)
                .build();
    }

    private Function createNativeApiFunction(final String environment, HashMap<String, String> lambdaEnvConfig) {
        return Function.Builder.create(this, "warranty-tracker-api")
                .functionName("warranty-tracker-api-" + environment)
                .description("Warranty Tracker: Native REST API: " + environment)
                .code(Code.fromAsset("../api/target/function.zip"))
                .timeout(Duration.seconds(15))
                .memorySize(512)
                // If building on M series mac
                .architecture(Architecture.ARM_64)
                // GraalVM specific
                .runtime(Runtime.PROVIDED_AL2023)
                .handler("not.used.for.native")
                .environment(lambdaEnvConfig)
                .build();
    }

    private void addHealthRoute(HttpApi httpApi) {
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/q")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("quarkus-q-root-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/q/{proxy+}") // captures /q/* and deeper
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("quarkus-q-endpoints-integration", apiFunction)
                        .build())
                .build());
    }
    
    private void addUserRoute(HttpApi httpApi) {
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user")
                .methods(List.of(HttpMethod.POST))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-post-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user")
                .methods(List.of(HttpMethod.PUT))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-put-integration", apiFunction)
                        .build())
                .build());
    }

    private void addCompanyRoute(HttpApi httpApi) {
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/company")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("company-get-integration", apiFunction)
                        .build())
                .build());
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/company/{id}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("company-get-one-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/company/search")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("company-search-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/company")
                .methods(List.of(HttpMethod.POST))
                .integration(HttpLambdaIntegration.Builder
                        .create("company-post-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/company/{id}")
                .methods(List.of(HttpMethod.PUT))
                .integration(HttpLambdaIntegration.Builder
                        .create("company-put-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/company/{id}")
                .methods(List.of(HttpMethod.DELETE))
                .integration(HttpLambdaIntegration.Builder
                        .create("company-delete-integration", apiFunction)
                        .build())
                .build());
    }
    
    private void addProductRoute(HttpApi httpApi) {
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/product")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("product-get-integration", apiFunction)
                        .build())
                .build());
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/product/{id}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("product-get-one-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/product/search")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("product-search-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/product")
                .methods(List.of(HttpMethod.POST))
                .integration(HttpLambdaIntegration.Builder
                        .create("product-post-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/product/{id}")
                .methods(List.of(HttpMethod.PUT))
                .integration(HttpLambdaIntegration.Builder
                        .create("product-put-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/product/{id}")
                .methods(List.of(HttpMethod.DELETE))
                .integration(HttpLambdaIntegration.Builder
                        .create("product-delete-integration", apiFunction)
                        .build())
                .build());
    }
    
    private void addUserProductRoute(HttpApi httpApi) {
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user-product")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-product-get-integration", apiFunction)
                        .build())
                .build());
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user-product/{id}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-product-get-one-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user-product/product/{productId}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-product-get-by-product-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user-product")
                .methods(List.of(HttpMethod.POST))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-product-post-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user-product/{id}")
                .methods(List.of(HttpMethod.PUT))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-product-put-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/user-product/{id}")
                .methods(List.of(HttpMethod.DELETE))
                .integration(HttpLambdaIntegration.Builder
                        .create("user-product-delete-integration", apiFunction)
                        .build())
                .build());
    }
    
    private void addWarrantyRoute(HttpApi httpApi) {
        // Regular warranty endpoints
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-get-integration", apiFunction)
                        .build())
                .build());
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/{id}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-get-one-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/status/{status}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-get-by-status-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/expiring")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-get-expiring-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty")
                .methods(List.of(HttpMethod.POST))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-post-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/{id}")
                .methods(List.of(HttpMethod.PUT))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-put-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/{id}")
                .methods(List.of(HttpMethod.DELETE))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-delete-integration", apiFunction)
                        .build())
                .build());
        
        // Admin warranty endpoints
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/admin/all")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-admin-get-all-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/admin/user/{userId}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-admin-get-by-user-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/admin/company/{companyId}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-admin-get-by-company-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/admin/expired")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-admin-get-expired-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/warranty/admin/expiring")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("warranty-admin-get-expiring-integration", apiFunction)
                        .build())
                .build());
    }
    
    private void addClaimRoute(HttpApi httpApi) {
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-get-integration", apiFunction)
                        .build())
                .build());
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim/{id}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-get-one-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim/warranty/{warrantyId}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-get-by-warranty-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim/status/{status}")
                .methods(List.of(HttpMethod.GET))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-get-by-status-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim")
                .methods(List.of(HttpMethod.POST))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-post-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim/{id}")
                .methods(List.of(HttpMethod.PUT))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-put-integration", apiFunction)
                        .build())
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/api/claim/{id}")
                .methods(List.of(HttpMethod.DELETE))
                .integration(HttpLambdaIntegration.Builder
                        .create("claim-delete-integration", apiFunction)
                        .build())
                .build());
    }
}
