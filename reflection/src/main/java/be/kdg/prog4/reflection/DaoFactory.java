package be.kdg.prog4.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class DaoFactory {
    public static Dao createDao(Class c) { //bedoeling van Class c?
        /* Person without annotations */
        Annotation[] annotations = c.getAnnotations();
        if (annotations.length == 0) {
            throw new IllegalArgumentException();
        } else {
            /* Person without id */
            int idCounter = 0;
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    System.out.println("it's present on the field");
                    idCounter++;
                }
                if (idCounter > 1) {
                    System.out.println("multiple id's!");
                    throw new IllegalArgumentException();
                }
                if (idCounter <1) {
                    System.out.println("exception!");
                    throw new IllegalArgumentException();
                }
            }
            System.out.println("count: " + idCounter);
        }

        InvocationHandler handler = new MyInvocationHandler(c);
        Object result = Proxy.newProxyInstance(Dao.class.getClassLoader(), new Class[]{Dao.class}, handler);
        //MyInterface result = Proxy.newProxyInstance(MyInterface.class.getClassLoader(), new Class[]{MyInterface.class}, handler); // Als je weet welke Interface(class) je wilt teruggeven
        return (Dao) result; //hier krijg je een Dao object terug
    }
}
