package org.kontza.fillerup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TriggerRestController {
    private TriggerService triggerService;
    private TaskExecutor taskExecutor;

    public TriggerRestController(TriggerService triggerService, TaskExecutor taskExecutor) {
        this.triggerService = triggerService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping("/trigger")
    private void triggerIt(@RequestParam Boolean defaultClient) {
        taskExecutor.execute(() -> {
            triggerService.triggerIt(defaultClient);
        });
    }
}
