package com.elinext.task.injector.impl;

import com.elinext.task.annotation.Inject;
import com.elinext.task.exception.BindingNotFoundException;
import com.elinext.task.exception.ConstructorNotFoundException;
import com.elinext.task.exception.TooManyConstructorsException;
import com.elinext.task.injector.Injector;
import com.elinext.task.provider.Provider;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.stream.Stream;

public class InjectorImpl implements Injector {
    private Map<Class<?>, Class<?>> diMap;

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return null;
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        try{
            Constructor<?> chosenConstructor = null;
            int injectedConstructors = 0;
            for (Constructor<?> constructor : impl.getConstructors()){
                if (constructor.isAnnotationPresent(Inject.class)){
                    injectedConstructors++;
                }
            }
            if (injectedConstructors > 1){
                throw new TooManyConstructorsException("There are 2 or more constructors with annotation "
                        + Inject.class);
            } else if (injectedConstructors == 0){
                boolean defaultConstructor = Stream.of(impl.getConstructors())
                        .anyMatch((constructor) -> constructor.getParameterCount() == 0);
                if (!defaultConstructor){
                    throw new ConstructorNotFoundException("Cannot find default constructor");
                } else {
                    System.out.println("Default constructor is found");
                    chosenConstructor = impl.getConstructor();
                }
            } else if (injectedConstructors == 1){
                System.out.println("One constructor with Inject annotation");
                chosenConstructor = Stream.of(impl.getConstructors())
                        .filter((constructor) -> constructor.isAnnotationPresent(Inject.class))
                        .findFirst()
                        .get();
            }
            Class<?>[] parametersTypes = chosenConstructor.getParameterTypes();
            for (Class<?> classType : parametersTypes){
                if (!diMap.containsKey(classType)){
                    throw new BindingNotFoundException("Binding for " + classType + " was not found");
                }
            }
        } catch (TooManyConstructorsException | ConstructorNotFoundException
                | NoSuchMethodException | BindingNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        if (diMap.containsValue(impl)){

        }
    }
}
