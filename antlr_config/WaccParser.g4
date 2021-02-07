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
stat: SKIP_LITER                                            #skip_stat
| type IDENT ASSIGN assignRHS                               #var_decl_stat
| assignLHS ASSIGN assignRHS                                #assign_stat
| READ assignLHS                                            #read_stat
| FREE expr                                                 #free_stat
| RETURN expr                                               #return_stat
| EXIT expr                                                 #exit_stat
| PRINT expr                                                #print_stat
| PRINTLN expr                                              #println_stat
| IF expr THEN stat ELSE stat FI                            #if_stat
| WHILE expr DO stat DONE                                   #while_stat
| stat SEMI_COLON stat                                      #sequence_stat
| BEGIN stat END                                            #new_scope_stat
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

expr: validForExpr                             #validExpr
| CHAR_LITER                                   #charLiter
| STR_LITER                                    #strLiter
| NULL                                         #pairLiter
| OPEN_PARENTHESES expr CLOSE_PARENTHESES      #paranExpr
| operationExpr                                #operExpr
| expr binaryOper expr                         #binOpExpr
;

operationExpr: operationExpr binOp1 operationExpr         #arithmeticExpr1
| operationExpr binOp2 operationExpr                      #arithmeticExpr2
| unaryOper expr                                          #unOpExpr
| validForExpr                                            #validForArithmetic
| OPEN_PARENTHESES operationExpr CLOSE_PARENTHESES        #paranArithmetic
;

validForExpr: IDENT                                         #identExpr
| (PLUS | MINUS)? INTEGER                                   #intLiter
| (TRUE | FALSE)                                            #boolLiter
| arrayElem                                                 #arrElemExpr
;

/*
Cant treat unary and binary operators as lexer rules, causes errors when
matching with expr
*/
unaryOper: NOT | MINUS | LEN | ORD | CHR ;
binaryOper: GT | GTE | LT | LTE | EQ | NE | AND | OR ;
binOp1: MULT | DIV | MOD ;
binOp2: PLUS | MINUS ;

arrayElem: IDENT (OPEN_SQUARE_BRACKETS expr CLOSE_SQUARE_BRACKETS)+ ;

//intLiter: (PLUS | MINUS)? INTEGER ;
//boolLiter: TRUE | FALSE ;
//charLiter: CHAR_LITER;
//strLiter: STR_LITER ;
//pairLiter: NULL ;

arrayLiter: OPEN_SQUARE_BRACKETS (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKETS ;

// For testing
prog: stat EOF ;
