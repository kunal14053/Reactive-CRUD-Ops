package com.reactive.application.goreactive.controller.v1;


import com.reactive.application.goreactive.constants.ItemConstants;
import com.reactive.application.goreactive.document.Item;
import com.reactive.application.goreactive.document.ItemCapped;
import com.reactive.application.goreactive.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class ItemStreamControllerTest {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    ReactiveMongoOperations mongoOperations;

    @Before
    public void setUp(){
        mongoOperations.dropCollection(ItemCapped.class);

        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20)
                        .size(5000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "randomItem"+i, 100.00+i))
                .take(5);

        itemReactiveCappedRepository.insert(itemCappedFlux)
                .doOnNext( item -> {
                    log.info("Inserted Item is " + item);
                }).blockLast();
    }


    //TODO: Not working
    @Test
    public void testStreamAllItems(){

        Flux<ItemCapped> itemCappedFlux = webTestClient.get()
                .uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult((ItemCapped.class))
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux).expectNextCount(5).thenCancel().verify();

    }



}
