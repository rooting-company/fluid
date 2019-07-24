package br.com.rooting.liquid.core;

public class FailToLiquifyException extends RuntimeException {

    FailToLiquifyException(Exception e) {
        super(e);
    }

}
