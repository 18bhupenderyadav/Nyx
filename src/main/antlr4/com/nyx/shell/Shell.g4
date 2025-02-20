grammar Shell;

// The entry point (for a full parse) – here we only need tokens.
command: token+ ;

token: SQ_STRING
     | DQ_STRING
     | WORD
     ;

SQ_STRING: '\'' (~'\'')* '\'' ;
// Note: In a production grammar, you might want to allow all characters except the quote.
// This simple rule takes any characters that are not a single quote.

//DQ_STRING: '"' ( ESC_SEQ | ~["\\\r\n] )* '"' ;
DQ_STRING: '"' ( ESC_SEQ | '\\' . | ~["\\] )* '"' ;

//fragment ESC_SEQ: '\\' (["\\$] | '\r'? '\n' ) ;
fragment ESC_SEQ: '\\' [\\"$] ;


WORD: ( '\\' . | ~[ \t\r\n'"] )+ ;

WS: [ \t\r\n]+ -> skip ;
