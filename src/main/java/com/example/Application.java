package com.example;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        Micronaut.
            build((String[]) null)
            .defaultEnvironments("dev").start();
    }
}
