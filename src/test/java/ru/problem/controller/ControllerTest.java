package ru.problem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.problem.Application;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.CollectionUtils.toMultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ControllerTest {
    @Autowired
    private WebApplicationContext webContext;
    private MockMvc mvc;

    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private ExecutorService pool = Executors.newCachedThreadPool(new CustomizableThreadFactory("test-pool-"));

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .build();
    }

    @Test
    public void testEnvironment() {
        assertThat(mvc).isNotNull();
    }

    private ControllerResponse tst(Long sleep) throws Exception {
        Map<String, List<String>> params = new HashMap<>();
        if (sleep != null) {
            params.put("sleep", singletonList(sleep.toString()));
        }
        String json = mvc
                .perform(get("/tst")
                        .params(toMultiValueMap(params))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(json).isNotBlank();
        return mapper.readValue(json, ControllerResponse.class);
    }

    @Test
    public void testFast() throws Exception {
        ControllerResponse response = pool.submit(() -> tst(null)).get();
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo("OK");
        assertThat(response.getThread()).isNotBlank();
        assertThat(response.getDuration()).isLessThanOrEqualTo(Duration.ofSeconds(1));
    }

    @Test
    public void testSlow() throws Exception {
        Duration sleep = Duration.ofSeconds(1);
        ControllerResponse response = pool.submit(() -> tst(sleep.toMillis())).get();
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo("OK");
        assertThat(response.getThread()).isNotBlank();
        assertThat(response.getDuration()).isGreaterThanOrEqualTo(sleep);
    }

    @Test
    public void testSlowBeforeFast() throws ExecutionException, InterruptedException {
        Duration sleep = Duration.ofSeconds(1);
        Future<ControllerResponse> slowFuture = pool.submit(() -> tst(sleep.toMillis()));
        Future<ControllerResponse> fastFuture = pool.submit(() -> tst(null));
        ControllerResponse slow = slowFuture.get();
        ControllerResponse fast = fastFuture.get();

        //Slow must be slow
        assertThat(slow.getResult()).isEqualTo("OK");
        assertThat(slow.getThread()).isNotBlank();
        assertThat(slow.getDuration()).isGreaterThanOrEqualTo(sleep);

        //todo Fast must be fast but it is slow too
        assertThat(fast.getResult()).isEqualTo("OK");
        assertThat(fast.getThread()).isNotBlank();
        assertThat(fast.getDuration()).isGreaterThanOrEqualTo(sleep);

        //But they works on different threads
        assertThat(fast.getThread()).isNotEqualTo(slow.getThread());
    }
}
