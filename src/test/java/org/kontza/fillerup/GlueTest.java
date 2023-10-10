package org.kontza.fillerup;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TriggerRestController.class)
public class GlueTest {
    private static final Logger logger = LoggerFactory.getLogger(GlueTest.class);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TriggerService triggerService;

    @Test
    public void bostik() throws Exception {
        MvcResult result = mvc.perform(get("/with-glue")).andExpect(status().isOk()).andReturn();
        logger.error(">>> result = {}", result.getResponse().getContentAsString());
    }
}
