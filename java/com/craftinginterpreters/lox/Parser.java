package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

/*
 * Parser relies on an finite precedence and associativity. The grammar
 * is defined below.
 *
 * expression -> equality ;
 * equality   -> comparison( ("!=" | "==") comparison )* ;
 * comparison -> term ( ( ">" | ">=" | "<" | "<=") term ) ;
 * term       -> factor ( ( "-" | "+") factor )*;
 * factor     -> unary ( ( "*" | "/" ) unary )* ;
 * unary      -> ( "!" | "-" ) unary
 *             | primary ;
 * primary    -> NUMBER | STRING | "true"
 *            | "false" | "nil" | "(" expression ")" | IDENTIFIER;
 *
 *
 * below is statement grammar.
 *
 * program         -> declaration* EOF ;
 * declaration     -> varDel |
 *                 | statement ;
 * varDel          -> "var" IDENTIFIER ("=" expression ? ";" ;
 * statement       -> expressionStmt | printStmt ;
 * expressionStmt  -> expression ";" ;
 * printStmt       -> "print" expression ";" ;
 *
 */
class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;
    
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return equality();
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }
        return statements;
    }

    private Stmt declaration() {
        if (match(VAR)) {
            return varDeclaration();
        }
        return statement();
    }

    private Stmt statement() {
        if (match(PRINT)) {
            return printStatement();
        }
        return expressionStatement();
    }

    private Stmt expressionStatement() {
        Expr expression = expression();
        consume(SEMICOLON, "Except ';' after expression");
        return new Stmt.Expression(expression);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect 'identifier' after keyword 'var'");
        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    /**
     * Returns a binary expression.
     * @return Expr
     */
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            Token opreator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, opreator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr =  new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG_EQUAL, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            if (!match(RIGHT_PAREN)) {
                throw error(peek(), "Expect right paren");
            }
            return expr;
        }
        if (match(IDENTIFIER)) return new Expr.Variable(previous());

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.err(token, message);
        return new ParseError();
    }
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the current token's type is required.
     * 
     * @param type TokenType
     * @return boolean
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;        
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    /**
     * Peeks current token.
     * 
     * @return Token
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Returns the previous token. Where is it used.
     * 
     * @return Token
     */
    private Token previous() {
        return tokens.get(current-1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }
}
