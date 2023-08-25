package com.craftinginterpreters.tool;

import java.util.Arrays;
import java.util.List;

class GenerateAST {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAST(outputDir, "Expr", Arrays.asList(
            "Binary: Expr left, Token operator, Expr right",
            "Grouping: Expr expression",
            "Literal: Object value"
        ));
    }

    private static void defineAST(String outputDir, String baseName, List<String> types) {

    }
}