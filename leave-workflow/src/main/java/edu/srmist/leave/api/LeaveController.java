package edu.srmist.leave.api;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class LeaveController {
    private final CamundaClient camundaClient;

    public LeaveController(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    @PostMapping("/leave")
    public Map<String, Object> applyLeave(@RequestBody LeaveRequest request) {
        ProcessInstanceEvent instance = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("student-leave")
                .latestVersion()
                .variables(request)
                .send()
                .join();

        return Map.of("processInstanceKey", instance.getProcessInstanceKey());
    }
}
