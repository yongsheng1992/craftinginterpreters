package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment environment) {
        this.enclosing = environment;
    }
    void define(String name, Object value) {
        this.values.put(name, value);
    }

    Object get(Token token) {
        if (values.containsKey(token.lexeme)) {
            return this.values.get(token.lexeme);
        }

        if (this.enclosing != null) {
            return this.enclosing.get(token);
        }
        throw new RuntimeError(token, "Undefined variable '" + token.lexeme + "'");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            this.values.put(name.lexeme, value);
            return;
        }

        if (this.enclosing != null) {
            this.enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
