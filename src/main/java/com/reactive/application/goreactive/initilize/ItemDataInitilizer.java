package com.reactive.application.goreactive.initilize;

import com.reactive.application.goreactive.document.Item;
import com.reactive.application.goreactive.document.ItemCapped;
import com.reactive.application.goreactive.repository.ItemReactiveCappedRepository;
import com.reactive.application.goreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitilizer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired //created during application runtime
    ReactiveMongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {

        mongoOperations.dropCollection(ItemCapped.class);

        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20)
                        .size(5000).capped());


    }

    public List<Item> data(){
        return Arrays.asList(
                new Item(null,"LG TV", 420.0),
                new Item(null,"Apple TV", 900.0),
                new Item(null,"Samsung TV", 100.0),
                new Item(null,"Oppo TV", 300.0));

    }

    private void initialDataSetup() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
        .subscribe((item) -> {
            System.out.println("Item inserted from CommandLineRunner with id: " + item.getId() );
        });

    }

    public void dataSetUpForCappedCollection(){

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "randomItem"+i, 100.00+i));

        //this insert will subscribe to the flux
        itemReactiveCappedRepository.insert(itemCappedFlux)
        .subscribe( item -> {
            log.info("Inserted Item is " + item);
        });

    }

}
