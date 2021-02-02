parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

program: BEGIN (func)* stat END EOF ;

/** Functions */
func: type IDENT OPEN_PARENTHESES paramList? CLOSE_PARENTHESES IS stat END ;

/** Parameters list */
paramList: param (COMMA param)* ;

/** Parameters */
param: type IDENT ;

/** Statements */
stat: SKIP_LITER
| type IDENT ASSIGN assignRHS
| assignLHS ASSIGN assignRHS
| READ assignLHS
| FREE expr
| RETURN expr
| EXIT expr
| PRINT expr
| PRINTLN expr
| IF expr THEN stat ELSE stat FI
| WHILE expr DO stat DONE
| BEGIN stat END
| stat SEMI_COLON stat
;

/** Assignments */
assignLHS: IDENT
| arrayElem
| pairElem
;

assignRHS: expr
| arrayLiter
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
| pairElem
| CALL IDENT OPEN_PARENTHESES argList? CLOSE_PARENTHESES
; 

argList: expr (COMMA expr)* ;

pairElem: FST expr | SND expr ;

/** Types */
type: baseType
| pairType
| type OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
;

baseType: INT
| BOOL
| CHAR
| STRING
;

pairType: PAIR OPEN_PARENTHESES pairElemType COMMA pairElemType CLOSE_PARENTHESES ;

pairElemType: baseType
| type OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
| PAIR 
;

expr: expr binaryOper expr
| intLiter
| boolLiter
| charLiter
| strLiter
| pairLiter
| IDENT
| arrayElem
| unaryOper expr
| expr binaryOper expr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES
;

unaryOper: NOT | MINUS | LEN | ORD | CHR ;

binaryOper: PLUS | MINUS | MULT | DIV | MOD | GT | GTE | LT | LTE EQ | AND | OR ;

arrayElem: IDENT (OPEN_SQUARE_BRACKETS expr CLOSE_SQUARE_BRACKETS)+ ;

intSign: PLUS | MINUS ;
intLiter: intSign? INTEGER ;
boolLiter: TRUE | FALSE ;
charLiter: CHAR_LITER;
strLiter: STR_LITER ;
pairLiter: NULL;

arrayLiter: OPEN_SQUARE_BRACKETS (expr (COMMA expr)*) CLOSE_SQUARE_BRACKETS ;

comment: COMMENT ;

// For testing
prog: (stat)* EOL EOF ;
