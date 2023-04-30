package edu.sou.cs452.drop;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.List;

public class DesktopLauncher {
    public static void main(String[] args) throws IOException, ScannerException {
        System.out.println(args[0]);
        if (args.length != 1) {
            System.out.println("Usage: drop script");
            System.exit(64); // [64]
        } else if (args.length == 1) {
            runFile(args[0]);
        }
    }

    private static void runFile(String path) throws IOException, ScannerException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void run(String source) throws ScannerException {
        Scanner scanner = new Scanner(source);

        // Run the scanner and save the tokens.
        List<Token> tokens = scanner.scanTokens();

        // Run the saved tokens through the parser.
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(statements);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Drop");

        config.useVsync(true);
        config.setForegroundFPS(60);

        // Set fullscreen mode
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        new Lwjgl3Application(interpreter, config); // Use the same interpreter instance
    }
}
