package dev.qeats.restaurant_management_service.service;

public interface InterfaceB {
    default void myDefaultMethod() {
        System.out.println("This is my default method");
    }
}
