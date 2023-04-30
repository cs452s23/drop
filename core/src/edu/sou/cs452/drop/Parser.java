//> Parsing Expressions parser
package edu.sou.cs452.drop;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import static edu.sou.cs452.drop.TokenType.*;

class ParserException extends Exception {
  public ParserException(String message) {
    super(message);
  }
}

class Parser {
  // < parse-error
  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();

    while (!isAtEnd()) {
      statements.add(statement());
    }

    return statements;
  }

  // > Statements and State parse-statement
  private Stmt statement() {
    // > Control Flow match-for
    // > Control Flow match-if
    if (peek().type == ITER)
      return iterStatement();
    if (peek().type == LEFT_BRACE)
      return orStatement();
    if (peek().type == INIT)
      return initStatement();
    if (peek().type == TRANSLATION)
      return translationStatement();
    if (peek().type == ROTATION)
      return rotationStatement();

    // We should never reach here...
    throw new RuntimeException("Unexpected token: " + peek());
  }

  private Stmt initStatement() {
    consume(INIT, "Expect init keyword.");
    consume(LEFT_PAREN, "Expect '(' after init statement.");
    consume(LEFT_BRACKET, "Expect '[' after init statement.");

    Float x1 = (Float) consume(NUMBER, "Expected x1 value.").literal;
    consume(COMMA, "Expect ',' after x1.");
    Float y1 = (Float) consume(NUMBER, "Expect y1 value.").literal;

    consume(RIGHT_BRACKET, "Expect ']' after y1.");
    consume(CROSS, "Expect 'x' after ].");
    consume(LEFT_BRACKET, "Expect '[' after x.");

    Float x2 = (Float) consume(NUMBER, "Expect x2 value.").literal;
    consume(COMMA, "Expect ',' after x2.");
    Float y2 = (Float) consume(NUMBER, "Expect y2 value.").literal;
    consume(RIGHT_BRACKET, "Expect ']' after y2.");

    consume(RIGHT_PAREN, "Expect ')' after init statement before opening block.");
    consume(SEMICOLON, "Expect ';' after init statement.");

    return new Stmt.Init(new Vector2(x1, y1), new Vector2(x2, y2));
  }

  private Stmt rotationStatement() {
    consume(ROTATION, "Expected ROTATION");
    consume(LEFT_PAREN, "Expect '(' after rotation statement.");

    Float x = (Float) consume(NUMBER, "Expect x value for rotation.").literal;
    consume(COMMA, "Expect ',' after x value.");
    Float y = (Float) consume(NUMBER, "Expect y value for rotation.").literal;
    consume(COMMA, "Expect ',' after y value.");
    Float angle = (Float) consume(NUMBER, "Expect angle value for rotation.").literal;
    consume(RIGHT_PAREN, "Expect ')' after rotation statement.");

    consume(SEMICOLON, "Expect ';' after rotation statement.");

    return new Stmt.Rotation(x, y, angle);
  }

  private Stmt orStatement() {

    List<Stmt> left = block();
    consume(OR, "Expect 'OR' after left-hand block.");
    List<Stmt> right = block();

    return new Stmt.Or(left, right);
  }

  private Stmt.Translation translationStatement() {
    consume(TRANSLATION, "Expect 'TRANSLATION'.");
    consume(LEFT_PAREN, "Expect '(' after translation statement.");

    float u = Float.parseFloat(consume(NUMBER, "Expect u value for translation.").lexeme);
    consume(COMMA, "Expect ',' after u value.");
    float v = Float.parseFloat(consume(NUMBER, "Expect v value for translation.").lexeme);

    consume(RIGHT_PAREN, "Expect ')' after translation statement before opening block.");
    consume(SEMICOLON, "Expect ';' after translation statement.");

    return new Stmt.Translation(u, v);
  }

  private Stmt iterStatement() {
    consume(ITER, "Expect 'ITER'.");
    List<Stmt> body = block();
    return new Stmt.Iter(body);
  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    consume(LEFT_BRACE, "Expect '{' to open block.");

    while (!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(statement());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();
    throw new RuntimeException(message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  // < check
  // > advance
  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  // < advance
  // > utils
  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
