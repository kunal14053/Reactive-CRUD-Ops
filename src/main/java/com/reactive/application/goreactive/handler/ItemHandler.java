package com.reactive.application.goreactive.handler;

import com.reactive.application.goreactive.document.Item;
import com.reactive.application.goreactive.document.ItemCapped;
import com.reactive.application.goreactive.repository.ItemReactiveCappedRepository;
import com.reactive.application.goreactive.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;


    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }


    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
        .body(fromObject(item))).switchIfEmpty(notFound);

        /*return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findById(id), Item.class);*/
    }


    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {

        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        return itemMono.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item),Item.class));

    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");
        Mono<Void> itemMono = itemReactiveRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Void.class);

    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
            .flatMap((item) -> {
            Mono<Item> itemMono = itemReactiveRepository.findById(id)
                    .flatMap(currentItem -> {
                        currentItem.setDescription(item.getDescription());
                        currentItem.setPrice(item.getPrice());
                        return itemReactiveRepository.save(currentItem);
                    });
            return itemMono;
        });

        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
        .body(fromObject(item))).switchIfEmpty(notFound);

    }



    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){

        throw new RuntimeException("RunTimeException Occurred");

    }


    public Mono<ServerResponse> itemsStream (ServerRequest serverRequest){

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);

    }


}
