package org.asciidoctor.internal;

//TODO: Replace with Java 8 function (NAATLAS-1954)
public interface AdocFunction<T> {
    void invoke(T obj);
}
