package com.foo.bar.batchexample.item;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.foo.bar.batchexample.model.Caisse;

public class Writer implements ItemWriter<Caisse> {

    private static final Logger log = LoggerFactory.getLogger(Writer.class);

    @Override
    public void write(
            final List<? extends Caisse> items)
            throws Exception {
        for (Caisse item : items) {
            log.info("WRITER: " + item.toString());
        }
    }
}
