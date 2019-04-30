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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.function.Function.identity;
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

    private ControllerResponse tst(
            Function<ControllerRequest.ControllerRequestBuilder, ControllerRequest.ControllerRequestBuilder> filler
    ) {
        ControllerRequest request = filler.apply(ControllerRequest.builder()).build();
        try {
            String json = mvc
                    .perform(get("/tst")
                            .params(toMultiValueMap(request.toParams()))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(json).isNotBlank();
            return mapper.readValue(json, ControllerResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Fail to request for: " + request, e);
        }
    }

    private CompletableFuture<ControllerResponse> tstA(
            Function<ControllerRequest.ControllerRequestBuilder, ControllerRequest.ControllerRequestBuilder> filler
    ) {
        return CompletableFuture
                .supplyAsync(() -> tst(filler), pool)
                .handle((res, thr) -> {
                    assertThat(res).isNotNull();
                    assertThat(res.getResult()).isEqualTo("OK");
                    assertThat(res.getThread()).isNotBlank().startsWith("test-pool-");
                    return res;
                });
    }

    @Test
    public void testFast() {
        ControllerResponse response = tstA(identity()).join();
        assertThat(response.getDuration()).isLessThanOrEqualTo(Duration.ofSeconds(1));
    }

    @Test
    public void testSlow() {
        Duration sleep = Duration.ofSeconds(1);
        ControllerResponse response = tstA(req -> req.sleep(sleep)).join();
        assertThat(response.getDuration()).isGreaterThanOrEqualTo(sleep);
    }

    private void testSlowBeforeFast(
            Duration sleep,
            Function<ControllerRequest.ControllerRequestBuilder, ControllerRequest.ControllerRequestBuilder> filler,
            BiConsumer<ControllerResponse, ControllerResponse> consumer
    ) throws InterruptedException {
        CompletableFuture<ControllerResponse> slowFuture = tstA(filler.andThen(req -> req.sleep(sleep)));
        //Чем больше спим - тем меньше висим в блокировке.
        //Но если совсем не спать, то быстрый запрос может прилететь раньше медленного - и тогда мы в блокировку совсем не попадём.
        Thread.sleep(20);
        CompletableFuture<ControllerResponse> fastFuture = tstA(filler);
        consumer.accept(slowFuture.join(), fastFuture.join());
    }

    @Test//Flaky
    public void testSlowBeforeFast_withNative_onSameB() throws InterruptedException {
        Duration sleep = Duration.ofSeconds(1);
        testSlowBeforeFast(sleep, req -> req.bId(1), (slow, fast) -> {
            //Slow must be slow
            assertThat(slow.getDuration()).isGreaterThanOrEqualTo(sleep);
            //todo Fast must be fast but it is slow too
            assertThat(fast.getDuration()).isGreaterThanOrEqualTo(sleep);
            //But they works on different threads
            assertThat(fast.getThread()).isNotEqualTo(slow.getThread());
        });
    }

    @Test
    public void testSlowBeforeFast_withNative_onDifferentB() throws InterruptedException {
        Duration sleep = Duration.ofSeconds(1);
        AtomicLong bId = new AtomicLong(1);
        testSlowBeforeFast(sleep, req -> req.bId(bId.getAndIncrement()), (slow, fast) -> {
            //Slow must be slow
            assertThat(slow.getDuration()).isGreaterThanOrEqualTo(sleep);
            //Fast must be fast and it is fast
            assertThat(fast.getDuration()).isLessThanOrEqualTo(sleep);
            //But they works on different threads
            assertThat(fast.getThread()).isNotEqualTo(slow.getThread());
        });
    }

    @Test
    public void testSlowBeforeFast_withoutNative_onSameB() throws InterruptedException {
        Duration sleep = Duration.ofSeconds(1);
        testSlowBeforeFast(sleep, req -> req.bId(1).withNative(false), (slow, fast) -> {
            //Slow must be slow
            assertThat(slow.getDuration()).isGreaterThanOrEqualTo(sleep);
            //Fast must be fast and it is fast
            assertThat(fast.getDuration()).isLessThanOrEqualTo(sleep);
            //But they works on different threads
            assertThat(fast.getThread()).isNotEqualTo(slow.getThread());
        });
    }

    @Test
    public void testSlowBeforeFast_withoutNative_onDifferentB() throws InterruptedException {
        Duration sleep = Duration.ofSeconds(1);
        AtomicLong bId = new AtomicLong(1);
        testSlowBeforeFast(sleep, req -> req.bId(bId.getAndIncrement()).withNative(false), (slow, fast) -> {
            //Slow must be slow
            assertThat(slow.getDuration()).isGreaterThanOrEqualTo(sleep);
            //Fast must be fast and it is fast
            assertThat(fast.getDuration()).isLessThanOrEqualTo(sleep);
            //But they works on different threads
            assertThat(fast.getThread()).isNotEqualTo(slow.getThread());
        });
    }
}
