package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {
        this.values.put(name, value);
    }

    Object get(Token token) {
        if (values.containsKey(token.lexeme)) {
            return this.values.get(token.lexeme);
        }

        throw new RuntimeError(token, "Undefined variable '" + token.lexeme + "'");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            this.values.put(name.lexeme, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
