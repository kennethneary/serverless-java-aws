package com.serverless.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DependencyInjector extends AbstractModule {

    public static final Injector injector = Guice.createInjector(new DependencyInjector());

    @Override
    protected void configure() {

    }
}
