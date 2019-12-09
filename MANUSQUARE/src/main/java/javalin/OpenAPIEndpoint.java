package javalin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.plugin.openapi.InitialConfigurationCreator;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.annotations.*;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.javalin.plugin.openapi.jackson.JacksonModelConverterFactory;
import io.javalin.plugin.openapi.jackson.JacksonToJsonMapper;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import javalin.controllers.ExmapleController;
import javalin.controllers.SemanticMatchingController;

public class OpenAPIEndpoint {
    public static void main(String[] args) {

  /*      JavalinJackson.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OpenApiOptions openApiOptions = new OpenApiOptions(new Info().version("1.0").description("My Application"))
                .activateAnnotationScanningFor("javalin.controllers")
                .path("/swagger-docs")
                .swagger(new SwaggerOptions("/swagger").title("My Swagger Documentation"))
                .reDoc(new ReDocOptions("/redoc").title("My ReDoc Documentation"));

        Javalin app = Javalin.create(config -> config.registerPlugin(new OpenApiPlugin(openApiOptions))).start(7070);

        app.post("/users", ExmapleController::create);
*/
        Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
        }).start();

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
        });

        app.post("/matching", SemanticMatchingController.PerformSemanticMatching);
        app.start(7000);



    }

    private static OpenApiOptions getOpenApiOptions() {
        InitialConfigurationCreator initialConfigurationCreator = () -> new OpenAPI()
                .info(new Info().version("1.0").description("Manusquare Matchmaking Service"));
                //.addServersItem(new Server().url("http://my-server.com").description("My Server"));

       OpenApiOptions opts =  new OpenApiOptions(initialConfigurationCreator)
                .path("/swagger-docs") // Activate the open api endpoint
               // .defaultDocumentation(doc -> { doc.json("500", MyError.class); }) // Lambda that will be applied to every documentation
                .activateAnnotationScanningFor("javalin.controllers") // Activate annotation scanning (Required for annotation api with static java methods)
                .toJsonMapper(JacksonToJsonMapper.INSTANCE) // Custom json mapper
                .modelConverterFactory(JacksonModelConverterFactory.INSTANCE) // Custom OpenApi model converter
            .swagger(new SwaggerOptions("/swagger").title("My Swagger Documentation")) // Activate the swagger ui
                .reDoc(new ReDocOptions("/redoc").title("My ReDoc Documentation"));// Active the ReDoc UI
        return opts;
    }
}

