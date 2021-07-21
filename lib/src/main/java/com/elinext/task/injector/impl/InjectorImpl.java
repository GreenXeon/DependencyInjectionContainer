package com.elinext.task.injector.impl;

import com.elinext.task.annotation.Inject;
import com.elinext.task.exception.BindingNotFoundException;
import com.elinext.task.exception.ConstructorNotFoundException;
import com.elinext.task.exception.TooManyConstructorsException;
import com.elinext.task.injector.Injector;
import com.elinext.task.provider.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class InjectorImpl implements Injector {
    private final Map<Class<?>, Class<?>> diMap = new HashMap<>();
    private final Map<Class<?>, Object> singletonInstances = new HashMap<>();

    @Override
    public <T> Provider<T> getProvider(Class<T> type) throws BindingNotFoundException{
        synchronized (this) {
            Object classInstance = null;
            try {
                Constructor<?> chosenConstructor;
                Class<?> impl = diMap.get(type);
                if (impl == null){
                    return null;
                }
                int injectedConstructors = (int) Stream.of(impl.getConstructors())
                        .filter((constructor) -> constructor.isAnnotationPresent(Inject.class))
                        .count();
                switch (injectedConstructors){
                    case 0:
                        chosenConstructor = impl.getConstructor();
                        classInstance = chosenConstructor.newInstance();
                        break;
                    case 1:
                        chosenConstructor = Stream.of(impl.getConstructors())
                                .filter((constructor) -> constructor.isAnnotationPresent(Inject.class))
                                .findFirst()
                                .get();
                        Class<?>[] parametersTypes = chosenConstructor.getParameterTypes();
                        List<Object> listOfInjectings = new LinkedList<>();
                        for (Class<?> classType : parametersTypes) {
                            if (!diMap.containsKey(classType)) {
                                throw new BindingNotFoundException("Binding for " + classType + " was not found");
                            }
                            listOfInjectings.add(
                                    this.getProvider(classType).getInstance()
                            );
                        }
                        classInstance = chosenConstructor.newInstance(listOfInjectings.toArray());
                        break;
                    default:
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                System.out.println(e.getMessage());
            }
            final Object finalClassInstance = classInstance;
            return () -> type.cast(finalClassInstance);
        }
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) throws TooManyConstructorsException,
            ConstructorNotFoundException {
        int injectedConstructors = (int) Stream.of(impl.getConstructors())
                .filter((constructor) -> constructor.isAnnotationPresent(Inject.class))
                .count();
        if (injectedConstructors > 1) {
            throw new TooManyConstructorsException("There are 2 or more constructors with annotation "
                    + Inject.class);
        } else if (injectedConstructors == 0) {
            boolean defaultConstructor = Stream.of(impl.getConstructors())
                    .anyMatch((constructor) -> constructor.getParameterCount() == 0);
            if (!defaultConstructor) {
                throw new ConstructorNotFoundException("Cannot find default constructor at " + impl.getName());
            }
        }
        diMap.put(intf, impl);
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
    }
}
