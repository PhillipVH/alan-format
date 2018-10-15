grammar Alan;

source : source_ EOF;

source_ : 'source' ID funcdef* body;

funcdef : 'function' ID '(' (type ID (',' type ID)*)? ')' ('to' type)? body;

body : 'begin' vardecl* statements 'end';

type : ('boolean' | 'integer') 'array'?;

vardecl : type ID (',' ID)* ';';

statements : 'relax' 
           | (statement (';' statement)*)
           ;

statement : assign 
          | call 
          | if_ 
          | input 
          | leave 
          | output 
          | while_
          ;

assign : ID ('[' simple ']')? ':=' (expr | 'array' simple);

call : 'call' ID '(' (factor (',' factor)*)? ')';

if_ : 'if' expr 'then' statements ('elsif' expr 'then' statements)* ('else' statements)? 'end';

input : 'get' ID ('[' simple ']')?;

leave : 'leave' expr?;

output : 'put' (STRING | expr) ('.' (STRING | expr))*;

while_ : 'while' expr 'do' statements 'end';

expr : simple (relop simple)?;

relop : '='
      | '>='
      | '>'
      | '<='
      | '<'
      | '<>'
      ;

simple : '-'? term (addop term)*;

addop : '-'
      | 'or'
      | '+';


term : factor (mulop factor)*;

mulop : 'and'
      | '/'
      | '*'
      | 'rem'
      ;

factor : ID ('[' simple ']' | '('(factor (',' factor)*)?')')?
       | NUM
       | '(' expr ')'
       | 'not' factor
       | 'true'
       | 'false'
       ;

/* The identifiers and numbers. */
ID    : LETTER (LETTER | DIGIT)*;
NUM   : DIGIT (DIGIT)*;
STRING : '"' .*? '"'; 

/*
STRING : '"' (ESC|.)*? '"'; // TODO Does Alan need escaped strings?
ESC : '\\"' | '\\\\' ; // 2-char sequences \" and \\ (page 78, Antlr Reference)
*/

/* The primitives. */
LETTER : [a-zA-Z_];
DIGIT : [0-9];

// COMMENTS : '{' .*? '}' -> skip;

/* Whitespace. */
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines


