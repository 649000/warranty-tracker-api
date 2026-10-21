package com.nazri;

import com.nazri.config.StackConfig;
import com.nazri.stack.APIGatewayStack;
import com.nazri.stack.APIStack;
import com.nazri.util.Constant;
import com.nazri.util.TagUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CdkTest {

    @BeforeAll
    static void setUpAll() throws IOException {
        File targetDir = new File("../api/target");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File zipFile = new File(targetDir, "function.zip");
        if (!zipFile.exists()) {
            zipFile.createNewFile();
        }
    }

    @Test
    void testStackConfig_Dev() {
        StackConfig config = CdkApp.getStackConfig(Constant.DEV);

        assertNotNull(config);
        assertFalse(config.getGraalvm());
        assertEquals(Constant.DEV, config.getTags().get(Constant.ENVIRONMENT));
        assertEquals(Constant.WARRANTY_TRACKER, config.getTags().get(Constant.PROJECT));
    }

    @Test
    void testStackConfig_Prd() {
        StackConfig config = CdkApp.getStackConfig(Constant.PRD);

        assertNotNull(config);
        assertTrue(config.getGraalvm());
        assertEquals(Constant.PRD, config.getTags().get(Constant.ENVIRONMENT));
        assertEquals(Constant.WARRANTY_TRACKER, config.getTags().get(Constant.PROJECT));
    }

    @Test
    void testStackConfig_DefaultToDev() {
        StackConfig config = CdkApp.getStackConfig("other-env");

        assertNotNull(config);
        assertFalse(config.getGraalvm());
        assertEquals(Constant.DEV, config.getTags().get(Constant.ENVIRONMENT));
    }

    @Test
    void testAPIGatewayStack() {
        App app = new App();
        StackConfig config = CdkApp.getStackConfig(Constant.DEV);

        APIGatewayStack gatewayStack = new APIGatewayStack(
                app,
                "test-api-gw-stack",
                config.getStackProps().stackName("test-api-gw-stack").build(),
                config
        );

        assertNotNull(gatewayStack.getHttpApi());

        Template template = Template.fromStack(gatewayStack);
        template.hasResourceProperties("AWS::ApiGatewayV2::Api", Map.of(
                "Name", "Warranty Tracker HTTP API Gateway - dev",
                "ProtocolType", "HTTP"
        ));
    }

    @Test
    void testAPIStack_Dev() {
        App app = new App();
        StackConfig config = CdkApp.getStackConfig(Constant.DEV);

        APIGatewayStack gatewayStack = new APIGatewayStack(
                app,
                "test-gw-stack",
                config.getStackProps().stackName("test-gw-stack").build(),
                config
        );

        APIStack apiStack = new APIStack(
                app,
                "test-api-stack",
                config.getStackProps().stackName("test-api-stack").build(),
                config,
                gatewayStack.getHttpApi()
        );

        Template template = Template.fromStack(apiStack);
        template.hasResourceProperties("AWS::Lambda::Function", Map.of(
                "MemorySize", 512,
                "Timeout", 15,
                "Runtime", "java21"
        ));
    }

    @Test
    void testAPIStack_Prd() {
        App app = new App();
        StackConfig config = CdkApp.getStackConfig(Constant.PRD);

        APIGatewayStack gatewayStack = new APIGatewayStack(
                app,
                "test-prd-gw-stack",
                config.getStackProps().stackName("test-prd-gw-stack").build(),
                config
        );

        APIStack apiStack = new APIStack(
                app,
                "test-prd-api-stack",
                config.getStackProps().stackName("test-prd-api-stack").build(),
                config,
                gatewayStack.getHttpApi()
        );

        Template template = Template.fromStack(apiStack);
        template.hasResourceProperties("AWS::Lambda::Function", Map.of(
                "MemorySize", 512,
                "Timeout", 15,
                "Runtime", "provided.al2023"
        ));
    }

    @Test
    void testAPIStack_UnsupportedEnv() {
        App app = new App();
        software.amazon.awscdk.StackProps.Builder stackProps = software.amazon.awscdk.StackProps.builder();
        java.util.Map<String, String> tags = new java.util.HashMap<>();
        tags.put(Constant.ENVIRONMENT, "invalid");
        tags.put(Constant.PROJECT, Constant.WARRANTY_TRACKER);
        StackConfig config = new StackConfig(stackProps, tags, false);

        APIGatewayStack gatewayStack = new APIGatewayStack(
                app,
                "test-invalid-gw-stack",
                config.getStackProps().stackName("test-invalid-gw-stack").build(),
                config
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new APIStack(
                    app,
                    "test-invalid-api-stack",
                    config.getStackProps().stackName("test-invalid-api-stack").build(),
                    config,
                    gatewayStack.getHttpApi()
            );
        });
    }

    @Test
    void testTagUtil() {
        App app = new App();
        StackConfig config = CdkApp.getStackConfig(Constant.DEV);
        APIGatewayStack stack = new APIGatewayStack(
                app,
                "test-tag-stack",
                config.getStackProps().stackName("test-tag-stack").build(),
                config
        );

        assertDoesNotThrow(() -> TagUtil.addTags(stack, config));
    }

    @Test
    void testConstants() {
        assertEquals("project", Constant.PROJECT);
        assertEquals("warranty-tracker", Constant.WARRANTY_TRACKER);
        assertEquals("environment", Constant.ENVIRONMENT);
        assertEquals("dev", Constant.DEV);
        assertEquals("sit", Constant.SIT);
        assertEquals("uat", Constant.UAT);
        assertEquals("prd", Constant.PRD);
    }
}
