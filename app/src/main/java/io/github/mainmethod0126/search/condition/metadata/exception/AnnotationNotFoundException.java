package io.github.mainmethod0126.search.condition.metadata.exception;

/**
 * This exception is thrown when a required annotation is not found.
 */
public class AnnotationNotFoundException extends Exception {

    /**
     * Constructs an AnnotationNotFoundException with no detail message.
     */
    public AnnotationNotFoundException() {
        super();
    }

    /**
     * Constructs an AnnotationNotFoundException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public AnnotationNotFoundException(String msg) {
        super(msg);
    }

}
