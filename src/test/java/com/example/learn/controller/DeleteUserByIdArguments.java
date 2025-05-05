package com.example.learn.controller;

import com.example.learn.model.User;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class DeleteUserByIdArguments implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                // Arguments for User 1
                Arguments.of(new User("John Doe", "john.doe@example.com", "password123")),
                // Arguments for User 2
                Arguments.of(new User("Jane Smith", "jane.smith@example.com", "password456")),
                // Arguments for User 3
                Arguments.of(new User("Alice Brown", "alice.brown@example.com", "password789"))
        );
    }
}
