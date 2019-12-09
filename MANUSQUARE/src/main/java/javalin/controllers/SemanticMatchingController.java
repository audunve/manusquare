package javalin.controllers;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import json.RequestForQuotation;
import org.eclipse.jetty.server.Authentication;
import ui.SemanticMatching_MVP;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Objects;

public class SemanticMatchingController {
    @OpenApi(
            //requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = RequestForQuotation.class)),
            formParams = @OpenApiFormParam(name="rfq", type = RequestForQuotation.class),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static Handler PerformSemanticMatching = ctx -> {
        String jsonInput = Objects.requireNonNull(ctx.formParam("rfq"));
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);

        SemanticMatching_MVP.performSemanticMatching(jsonInput, 10, writer, false, true);
        System.out.println(sw.toString());
        ctx.json(sw.toString());
    };
}
