package org.hokusai.embedding;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.web.WebView;
// import javafx.stage.Stage;

/**
 * A basic polyglot application that tries to exercise a simple hello world style program in all installed languages.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            Set<String> languages = context.getEngine().getLanguages().keySet();
            for (String id : languages) {
                System.out.println("Initializing language " + id);
                context.initialize(id);
                switch (id) {
                    case "python":
                        context.eval("python", "print('Hello Python!')");
                        break;
                    case "js":
                        context.eval("js", "print('Hello JavaScript!');");
                        break;
                    case "ruby":
                        context.eval("ruby", "require 'date'; puts \"Hello Ruby! #{DateTime.now}\"");
                        break;
                    case "wasm":
                        // with wasm we compute factorial
                        context.eval(Source.newBuilder("wasm", Main.class.getResource("factorial.wasm")).name("main").build());
                        Value factorial = context.getBindings("wasm").getMember("main").getMember("fac");
                        System.out.println("wasm: factorial(20) = " + factorial.execute(20L));
                        break;
                    case "java":
                        // with Java we invoke System.out.println reflectively.
                        Value out = context.getBindings("java").getMember("java.lang.System").getMember("out");
                        out.invokeMember("println", "Hello Espresso Java!");
                        break;
                }
            }
        }
    }
}