//> Scanning scanner-class
package edu.sou.cs452.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.sou.cs452.drop.TokenType.*; // [static-import]

class ScannerException extends Exception {
  public ScannerException(String message) {
    super(message);
  }
}

class Scanner {
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("iter", ITER);
    keywords.put("init", INIT);
    keywords.put("translation", TRANSLATION);
    keywords.put("or", OR);
    keywords.put("rotation", ROTATION);
  }

  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  // > scan-tokens
  List<Token> scanTokens() throws ScannerException {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() throws ScannerException {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '[':
        addToken(LEFT_BRACKET);
        break;
      case ']':
        addToken(RIGHT_BRACKET);
        break;
      case 'x':
        addToken(CROSS);
        break;
      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;
      default:
        if (isDigit(c) || c == '-') {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          throw new ScannerException(" Unexpected character " + c);
        }
        break;
    }
  }

  // < scan-token
  // > identifier
  private void identifier() {
    while (isAlphaNumeric(peek()))
      advance();

    /*
     * Scanning identifier < Scanning keyword-type
     * addToken(IDENTIFIER);
     */
    // > keyword-type
    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    assert (type != null);
    addToken(type);
    // < keyword-type
  }

  // < identifier
  // > number
  private void number() {
    boolean isNegative = false;
    if (peek() == '-') {
      advance();
      isNegative = true;
    }

    while (isDigit(peek()))
      advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek()))
        advance();
    }

    Float value = Float.parseFloat(source.substring(start, current));
    if (isNegative)
      value = -value;

    addToken(NUMBER, value);
  }

  // < match
  // > peek
  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  // < peek
  // > peek-next
  private char peekNext() {
    if (current + 1 >= source.length())
      return '\0';
    return source.charAt(current + 1);
  } // [peek-next]
  // < peek-next
  // > is-alpha

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  // < is-alpha
  // > is-digit
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  } // [is-digit]
  // < is-digit
  // > is-at-end

  private boolean isAtEnd() {
    return current >= source.length();
  }

  // < is-at-end
  // > advance-and-add-token
  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
  // < advance-and-add-token
}
