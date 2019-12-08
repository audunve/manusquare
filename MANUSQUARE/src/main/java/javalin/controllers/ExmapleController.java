package javalin.controllers;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;

public class ExmapleController {

    @OpenApi(
            path = "/users",
            method = HttpMethod.POST,
            queryParams = {
                    @OpenApiParam(name = "my-query-param")
            },
            responses = {
                    @OpenApiResponse(status = "201", content = @OpenApiContent(from = String.class))
            }
    )
    public static void create(Context ctx) {

    }

}