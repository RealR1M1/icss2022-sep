grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';



//--- PARSER: ---
stylesheet: variableassignment* stylerule+;
stylerule: selector OPEN_BRACE  (variableassignment*)? declaration+ (ifstatement*)? (declaration+)? CLOSE_BRACE;
selector: LOWER_IDENT | ID_IDENT | CLASS_IDENT;
declaration: property COLON expression SEMICOLON;
ifstatement: IF BOX_BRACKET_OPEN variablereference BOX_BRACKET_CLOSE OPEN_BRACE (declaration+ (ifstatement)? (elsestatement)?)+ CLOSE_BRACE;
elsestatement: CLOSE_BRACE ELSE OPEN_BRACE declaration+;
property: LOWER_IDENT;

expression: expression MUL expression #multiexpression | expression PLUS expression #addexpression | expression MIN expression #subtractexpression | literal #literalexpression | variablereference #variableexpression;

literal: PIXELSIZE #pixelSize | COLOR #color | bool #boolean | SCALAR #scalar | PERCENTAGE #percentage;
variableassignment: variablereference ASSIGNMENT_OPERATOR expression SEMICOLON;
variablereference: CAPITAL_IDENT;
bool: TRUE | FALSE;

