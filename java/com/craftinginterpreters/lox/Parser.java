package com.craftinginterpreters.lox;

import java.util.List;
import static com.craftinginterpreters.lox.TokenType.*;

/*
 * Parser relies on an finite precedence and associativity. The grammer is defined below.
 * expression -> equality ;
 * equality   -> comparsion( ("!=" | "==") comparsion )* ;
 * comparsion -> term ( ( ">" | ">=" | "<" | "<=") term ) ;
 * term       -> factor ( ( "-" | "+") factor );
 * factor     -> unary ( ( "!" | "-" ) unary )* ;
 * unary      -> ( "!" | "-" ) unary
 *             | primary ;
 * primary    -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */
class Parser {
    private final List<Token> tokens;
    private int current = 0;
    
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return equality();
    }

    /**
     * Returns a binary expression.
     * (a + b == 1) == (a + b != 1)
     * @return
     */
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token opreator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, opreator, right);
        }

        return expr;
    }

    private Expr comparison() {
        
    }

    private Expr primary() {
        Token type = peek();
        switch (type.type) {
            case NUMBER: return new Expr.Literal(type);
            case STRING: return new Expr.Literal(type);
            default:
                Lox.err(type, null);
        }
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
     * @param type
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
     * Returns the previous toekn. Where is it used.
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
