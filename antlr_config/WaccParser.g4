parser grammar WaccParser;

options {
  tokenVocab=WaccLexer;
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
| stat SEMI_COLON stat
| BEGIN stat END
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
type: baseType  # TypeBaseType
| arrayType     # TypeArrayType
| pairType      # TypePairType
;

baseType: INT
| BOOL
| CHAR
| STRING
;

arrayType: (baseType | pairType) (OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS)+ ;

pairType: PAIR OPEN_PARENTHESES pairElemType COMMA pairElemType CLOSE_PARENTHESES ;

pairElemType: baseType
| arrayType
| PAIR
;

expr: (PLUS | MINUS)? INTEGER                  #intLiter
| (TRUE | FALSE)                               #boolLiter
| CHAR_LITER                                   #charLiter
| STR_LITER                                    #strLiter
| NULL                                         #pairLiter
| IDENT                                        #identExpr
| arrayElem                                    #arrElemExpr
| unaryOper expr                               #unOpExpr
| expr binaryOper expr                         #binOpExpr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES      #paranExpr
;

/*
Cant treat unary and binary operators as lexer rules, causes errors when
matching with expr
*/
unaryOper: NOT | MINUS | LEN | ORD | CHR ;
binaryOper: PLUS | MINUS | MULT | DIV | MOD | GT | GTE | LT | LTE | EQ | NE | AND | OR ;

arrayElem: IDENT (OPEN_SQUARE_BRACKETS expr CLOSE_SQUARE_BRACKETS)+ ;

//intLiter: (PLUS | MINUS)? INTEGER ;
//boolLiter: TRUE | FALSE ;
//charLiter: CHAR_LITER;
//strLiter: STR_LITER ;
//pairLiter: NULL ;

arrayLiter: OPEN_SQUARE_BRACKETS (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKETS ;

// For testing
prog: stat EOF ;