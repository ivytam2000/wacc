lexer grammar WaccLexer;

// Program
END: 'end' ;
BEGIN: 'begin' ;

// General
IS: 'is' ;
COMMA: ',' ;
ASSIGN: '=' ;
SKIP_LITER: 'skip' ;
READ: 'read' ;
FREE: 'free' ;
RETURN: 'return' ;
EXIT: 'exit' ;
PRINT: 'print' ;
PRINTLN: 'println' ;
IF: 'if' ;
THEN: 'then' ;
ELSE: 'else' ;
FI: 'fi' ;
WHILE: 'while' ;
DO: 'do' ;
DONE: 'done' ;
SEMI_COLON: ';' ;
NEWPAIR: 'newpair' ;
CALL: 'call' ;
OPEN_SQUARE_BRACKETS: '[' ;
CLOSE_SQUARE_BRACKETS: ']' ;
PAIR: 'pair' ;
FST: 'fst' ;
SND: 'snd' ;
FOR: 'for';
IN: 'in';
UNTIL: 'until';

// Base types
INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;

// Binary operators
PLUS: '+' ;
MINUS: '-' ;
MULT: '*' ;
DIV: '/' ;
MOD: '%' ;
GT: '>' ;
GTE: '>=' ;
LT: '<' ;
LTE: '<=' ;
EQ: '==' ;
NE: '!=' ;
AND: '&&' ;
OR: '||' ;
BIT_AND: '&' ;
BIT_OR: '|' ;

// Unary operators
NOT: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;
BIT_NOT: '~' ;

// Brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

// Booleans
TRUE: 'true' ;
FALSE: 'false' ;

NULL: 'null' ;

// Characters and numbers
fragment DIGIT: '0'..'9' ;
fragment BINARY_FORMATTER: '0b' ;
fragment BINARY_DIGIT: '0' | '1' ;
fragment HEXADECIMAL_FORMATTER: '0x' ;
fragment HEXADECIMAL_DIGIT: DIGIT | 'a'..'f' | 'A'..'F' ;
fragment LOWERCASE: 'a'..'z' ;
fragment UPPERCASE: 'A'..'Z' ;
fragment UNDERSCORE: '_' ;
fragment SINGLE_QUOTE: '\'' ;
fragment DOUBLE_QUOTE: '"' ;

INTEGER: DIGIT+ ;
BINARY_INTEGER: BINARY_FORMATTER BINARY_DIGIT+ ;
HEXADECIMAL_INTEGER: HEXADECIMAL_FORMATTER HEXADECIMAL_DIGIT+ ;

fragment ESCAPED_CHARACTER:
    '\\'
    ('0'
    | 'b'
    | 'n'
    | 'f'
    | 'r'
    | DOUBLE_QUOTE
    | SINGLE_QUOTE
    | '\\');
fragment CHARACTER: ~( '\'' | '"' | '\\' ) | ESCAPED_CHARACTER ;

// Match and discard whitespace
WS : (' '|'\t'|'\r'|'\n')+ -> channel(HIDDEN) ;

IDENT:
    (UNDERSCORE
    | LOWERCASE
    | UPPERCASE)
    (UNDERSCORE
    | LOWERCASE
    | UPPERCASE
    | DIGIT)*;

CHAR_LITER: SINGLE_QUOTE CHARACTER SINGLE_QUOTE;
STR_LITER: DOUBLE_QUOTE CHARACTER* DOUBLE_QUOTE;

// Match and discard comments which start with '#' followed by a sequence of 0 or more of any characters
// .*? is a wildcard which consumes everything
COMMENT: '#' .*? '\n' -> skip ;
