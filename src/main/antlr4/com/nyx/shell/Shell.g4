/**
 * Grammar for parsing shell commands.
 * Supports:
 * - Single quoted strings: No escape processing ('foo\bar' -> foo\bar)
 * - Double quoted strings: Escape sequences for \, $, " ("\$foo" -> $foo)
 * - Unquoted words: Basic token with escape support
 * - Comments: Lines starting with #
 * - Whitespace handling and tokenization
 */
grammar Shell;

// Root rule for parsing a complete command line
command: token+ ;

// Token types supported in the shell grammar
token: SQ_STRING    // Single quoted string ('no escapes')
     | DQ_STRING    // Double quoted string ("supports \"escapes\"")
     | WORD         // Unquoted text with escape support
     | COMMENT      // Shell comments
     ;

// Single quoted strings - content is taken literally
SQ_STRING: '\'' (~['\r\n])* '\'' ;

// Double quoted strings - support escaping of special characters
DQ_STRING: '"' ( ESC_SEQ | '\\' . | ~["\\] )* '"' ;

// Escape sequences for double quoted strings
fragment ESC_SEQ: '\\' ["\\\r\n$] ;

// Unquoted words - support escaping of special characters
WORD: ( '\\' . | ~[ \t\r\n'"#] )+ ;

// Comments - lines starting with #, skipped in parsing
COMMENT: '#' ~[\r\n]* -> skip ;

// Whitespace - skipped in parsing
WS: [ \t\r\n]+ -> skip ;
