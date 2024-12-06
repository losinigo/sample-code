package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.time.Duration;
import java.util.*;

import com.microsoft.durabletask.*;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

/**
 * Please follow the below steps to run this durable function sample
 * 1. Send an HTTP GET/POST request to endpoint `StartHelloCities` to run a durable function
 * 2. Send request to statusQueryGetUri in `StartHelloCities` response to get the status of durable function
 * For more instructions, please refer https://aka.ms/durable-function-java
 * 
 * Please add com.microsoft:durabletask-azure-functions to your project dependencies
 * Please add `"extensions": { "durableTask": { "hubName": "JavaTestHub" }}` to your host.json
 */
public class MQOrchestrator {
    /**
     * This HTTP-triggered function starts the orchestration.
     */
    @FunctionName("MQMessageOrchestrate")
    public HttpResponseMessage mqMessageOrchestrate(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET,
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
            final ExecutionContext context) {

        context.getLogger().info("Java HTTP trigger processed a request.");

        // Initialize durable task client
        DurableTaskClient client = durableContext.getClient();

        // Start orchestration
        String instanceId = client.scheduleNewOrchestrationInstance("TestOrchestrator");

        // Output instance id
        context.getLogger().info("Created new Java orchestration with instance ID = " + instanceId);

        // Return status of orchestration in response
        return durableContext.createCheckStatusResponse(request, instanceId);
    }

    /**
     * This is the orchestrator function, which can schedule activity functions, create durable timers,
     * or wait for external events in a way that's completely fault-tolerant.
     */
    @FunctionName("TestOrchestrator")
    public void testOrchestrator(
            @DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx, ExecutionContext context) {

        System.out.println("Processing stuff...");

        ctx.createTimer(Duration.ofSeconds(5)).await();

        ctx.continueAsNew(null);

    }

}