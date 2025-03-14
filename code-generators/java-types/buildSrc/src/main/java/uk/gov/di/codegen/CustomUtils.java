package uk.gov.di.codegen;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CustomUtils {
    private CustomUtils() {}

    /**
     * Read a {@link java.util.Iterator} as a {@link java.util.stream.Stream}.
     * @param iterator The {@link java.util.Iterator} to read
     * @return Instance of the {@link java.util.stream.Stream} to read the
     * {@link java.util.Iterator}.
     * @param <T> Type of the items in the {@link java.util.Iterator}
     */
    public static <T> Stream<T> toStream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }
}
