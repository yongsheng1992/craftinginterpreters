package com.craftinginterpreters.tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAST(outputDir, "Expr", Arrays.asList(
            "Assign: Token name, Expr value",
            "Binary: Expr left, Token operator, Expr right",
            "Call: Expr callee, Token paren, List<Expr> arguments",
            "Get: Expr object, Token name",
            "Grouping: Expr expression",
            "Literal: Object value",
            "Logical: Expr left, Token operator, Expr right",
            "Set: Expr Object, Token name, Expr value",
            "Supper: Token keyword, Token method",
            "This: Token keyword",
            "Unary: Token operator, Expr right",
            "Variable: Token name"
        ));
    }

    private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + File.separator + outputDir + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.creftinginterpreters.lox");
        writer.println();
        writer.println("import java.util.List;");
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        writer.println();
        
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
            writer.println(" static class " + className + " extends " + baseName + " {");

            // Constructor
            writer.println("    " + className + "(" + fieldList + ") {");
            String[] fields = fieldList.split(", ");
            for (String field : fields) {
                String name = field.split(" ")[1];
                writer.println("    this." + name + " = " + name + ";");
            }
            writer.println("}");
            
            // Visitor pattern
            writer.println();
            writer.println("    @Override");
            writer.println("    <R> R accept(Visitor<R> vistor) {");
            writer.println("    return visitor.visit" + className + baseName + "(this);");
            writer.println("    }");

            // Fields
            writer.println();
            for (String field : fields) {
                writer.println("    final " + field + ";");
            }

    
            writer.println("  }");
    }

    interface PastryVistor {
        void visitBeignet(Beignet beignet);
        void visitCruller(Cruller cruller);
    }

    abstract class Pastry {
        abstract void accept(PastryVistor visitor);
    }

    class Beignet extends Pastry {
        @Override
        void accept(PastryVistor vistor) {
            vistor.visitBeignet(this);
        }
    }

    class Cruller extends Pastry {
        @Override
        void accept(PastryVistor vistor) {
            vistor.visitCruller(this);
        }
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("  }");
    }

}