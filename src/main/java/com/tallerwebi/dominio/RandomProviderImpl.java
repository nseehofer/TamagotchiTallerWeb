package com.tallerwebi.dominio;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomProviderImpl implements RandomProvider {

    private Random random = new Random();

    @Override
    public double obtenerRandom() {
        return random.nextDouble();
    }
}
