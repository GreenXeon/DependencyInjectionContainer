package com.elinext.task.injector;

import com.elinext.task.exception.BindingNotFoundException;
import com.elinext.task.exception.ConstructorNotFoundException;
import com.elinext.task.exception.TooManyConstructorsException;
import com.elinext.task.provider.Provider;

public interface Injector {
    <T> Provider<T> getProvider(Class<T> type) throws ConstructorNotFoundException, BindingNotFoundException, TooManyConstructorsException; //получение инстанса класса со всеми иньекциями по классу интерфейса

    <T> void bind(Class<T> intf, Class<? extends T> impl) throws TooManyConstructorsException, ConstructorNotFoundException; //регистрация байндинга по классу интерфейса и его реализации

    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl); //регистрация синглтон класса
}

