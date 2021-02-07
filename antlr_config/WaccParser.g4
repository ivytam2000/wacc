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

assignRHS: expr                                                     #expr_assignRHS
| arrayLiter                                                        #arrayLiter_assignRHS
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES        #newPair_assignRHS
| pairElem                                                          #pairElem_assignRHS
| CALL IDENT OPEN_PARENTHESES argList? CLOSE_PARENTHESES            #call_assignRHS
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

pairElemType: baseType  #PairElemTypeBaseType
| arrayType             #PairElemTypeArrayType
| PAIR                  #PairElemTypePairToken
;

expr: (PLUS | MINUS)? INTEGER                  #intLiter
| (TRUE | FALSE)                               #boolLiter
| CHAR_LITER                                   #charLiter
| STR_LITER                                    #strLiter
| NULL                                         #pairLiter
| IDENT                                        #identExpr
| arrayElem                                    #arrElemExpr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES      #paranExpr
| unaryOper expr                               #unOpExpr
| expr arithmeticOper1 expr                    #arithOpExpr_1
| expr arithmeticOper2 expr                    #arithOpExpr_2
| expr binaryOper expr                         #binOpExpr
;

/*
Cant treat unary and binary operators as lexer rules, causes errors when
matching with expr
*/
unaryOper: NOT | MINUS | LEN | ORD | CHR ;
arithmeticOper1: MULT | DIV | MOD;
arithmeticOper2: PLUS | MINUS;
binaryOper: GT | GTE | LT | LTE | EQ | NE | AND | OR ;

arrayElem: IDENT (OPEN_SQUARE_BRACKETS expr CLOSE_SQUARE_BRACKETS)+ ;

//intLiter: (PLUS | MINUS)? INTEGER ;
//boolLiter: TRUE | FALSE ;
//charLiter: CHAR_LITER;
//strLiter: STR_LITER ;
//pairLiter: NULL ;

arrayLiter: OPEN_SQUARE_BRACKETS (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKETS ;

// For testing
prog: stat EOF ;
