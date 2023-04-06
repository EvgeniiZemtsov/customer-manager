package com.zemtsov.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
