package com.reactive.application.goreactive.controller.v1;

import com.reactive.application.goreactive.document.ItemCapped;
import com.reactive.application.goreactive.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


import static com.reactive.application.goreactive.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

@RestController
@Slf4j
public class ItemStreamController {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getAllItems(){

       return itemReactiveCappedRepository.findItemsBy();

    }


}
