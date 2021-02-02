lexer grammar BasicLexer;

/** Program */
END: 'end' ;
BEGIN: 'begin' ;

/** General */
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

/** Base types */
INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;


/** Unary operators */
NOT: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;

/** Binary operators */
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
AND: '&&' ;
OR: '||' ;

/** Brackets */
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

/** Booleans */
TRUE: 'true' ;
FALSE: 'false' ;

NULL: 'null' ;

/** Characters and numbers */
fragment DIGIT: '0'..'9' ;
fragment LOWERCASE: 'a'..'z';
fragment UPPERCASE: 'A'..'Z';
fragment UNDERSCORE: '_';
fragment SINGLE_QUOTE: '\'';
fragment DOUBLE_QUOTE: '"';

INTEGER: DIGIT+ ;

IDENT:
    (UNDERSCORE
    | LOWERCASE
    | UPPERCASE)
    (UNDERSCORE
    | LOWERCASE
    | UPPERCASE
    | DIGIT)*;

fragment ESCAPED_CHARACTER:
    ('0'
    | 'b'
    | 'n'
    | 'f'
    | 'r'
    | DOUBLE_QUOTE
    | SINGLE_QUOTE
    | '\\');
fragment CHARACTER: ~( '\'' | '"' | '\\' ) | '\\' ESCAPED_CHARACTER ;

CHAR_LITER: SINGLE_QUOTE CHARACTER SINGLE_QUOTE;
STR_LITER: DOUBLE_QUOTE CHARACTER* DOUBLE_QUOTE;

EOL: '\n' ;

/** Match and discard comments which start with '#', followed by a sequence of 
0 or more of any characters (.*? is a wildcard which consumes everything) */
COMMENT: '#' .*? EOL -> skip ;

/** Match and discard whitespace */
WS : (' '|'\t'|'\r'|'\n')+ -> skip ; 