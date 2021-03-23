package es.ucm.fdi.iw.model;

/**
 * Used to json-ize objects
 */
public interface Transferable<T> {
    T toTransfer();
}
