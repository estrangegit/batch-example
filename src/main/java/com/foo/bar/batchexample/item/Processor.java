package com.foo.bar.batchexample.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.foo.bar.batchexample.model.Caisse;

public class Processor implements ItemProcessor<Caisse, Caisse> {

    private static final Logger log = LoggerFactory.getLogger(Processor.class);

    @Override
    public Caisse process(
            final Caisse item)
            throws Exception {
        log.info("PROCESSOR: " + item.toString());
        return item;
    }

}
