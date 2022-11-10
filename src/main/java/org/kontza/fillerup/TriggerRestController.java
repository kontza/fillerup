package org.kontza.fillerup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TriggerRestController {
    private static final Logger logger = LoggerFactory.getLogger(TriggerRestController.class);
    private TriggerService triggerService;
    private TaskExecutor taskExecutor;

    public TriggerRestController(TriggerService triggerService, TaskExecutor taskExecutor) {
        this.triggerService = triggerService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping("/trigger")
    private void triggerIt() {
        taskExecutor.execute(() -> {
            try {
                triggerService.triggerIt();
            } catch (IOException e) {
                logger.error(">>> Trigger failed: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}
