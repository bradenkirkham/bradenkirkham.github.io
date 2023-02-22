##MSDScript Documentation
####Braden Kirkham

MSDScript is a simple scripting language capable of creating and solving simple math expressions.  Currently, MSDScript can handle Addition, Multiplication, Comparison, if/then expressions, let expressions, and functions.

###Instalation guide

To install MSDScript, first, use the terminal to navigate to the MSDScript file on your computer.  Once you are in the MSDScript directory, run the following command to create an MSDScript executable.

	$make
This command will create an MSDScript executable in the same directory that can be run to use MSDScript in the terminal.  The use of MSDScript in the terminal will be discussed later.

If you wish to create a library that can be used to link MSDScript with other programs you are making, you must run the following command in the same directory in the terminal.
	
	$ar -ruv libmsdscript.a val.o parse.o Step.o Expr.o Env.o cont.o
	
This will create an MSDScript .a library file named 'libmsdscript.a'.  This library folder will allow you to link MSDScript into your programs by including a few of the header files. The basic set of files you'll need to include are, "Step.h", "parse.h", and "val.h".  The use of MSDScript in your programs will be discussed later in the document.

###MSDScript in Terminal

To use the MSDScript executable, you must first be in the MSDScript directory. To run the executable, you must first type './msdscript' followed by the command you wish to execute.  MSDScript can perform the following commands.

	./msdscript --help
Running the help command will display a list of all possible commands.  This list is just meant to remind the user of all possible commands; no explanation is given with the commands.

	./msdscript --test
Using this command will run all of the built-in tests used in developing MSDScript. This command does not have much use for the user.
	
	./msdscript --interp
This command takes an expression and returns its result.  Interp only works with complete expressions and will not work with expressions with free variables.  The proper syntax for writing expressions will be discussed later in this document.
	
	./msdscript --print
This command also takes an expression and will accept any expression, even those with free variables.  The print command will format and print the expression entered.

After typing './msdscript' and the desired command, hit enter; in the case of the interp and print commands, you will then be able to enter an expression. After entering the desired expression, hit the return key and then CMD d.  The program will then execute and print the result or an error if the input expression is invalid.  The next session will go over the valid syntax of expressions in MSDScript.

###Syntax

```
<expr>  = <number>
        | <boolean>
        | <expr> == <expr>
        | <expr> + <expr>
        | <expr> * <expr>
        | <expr> ( <expr> )
        | <variable>
        | _let <variable> = <expr> _in <expr>
        | _if <expr> _then <expr> _else <expr>
        | _fun (<variable>) <expr>
```
 
MSDScript solves and creates math expressions through the use of various expressions forms(expr).  These expressions can be nested within one another to allow for the solving of more complex problems.  The above figure shows the general structure of MSDScript expressions.  Examples of each form will be the focus of the rest of this section.

Note - When nesting expressions in MSDScript parenthesis are used to show the order of operations.

Number Expressions:
	The simplest form of Expression.  This is denoted simply by any integer.  Number Expressions will take negative integers as well as positive.  The following would be a correct use of number expressions.
	
	45
	-256
Note - Using negative integers is the only way you can subtract in MSDScript.
	
Boolean Expressions (True/False):
	Boolean expressions are either true or false.  The proper syntax for Booleans is as follows.
	
	_true
	_false

Equality Expressions:
	An equality expression is used in MSDScript to compare two expressions. An equality expression follows the same syntax as most other languages using a '==' as shown below.
	
	5==5
	5==x
	
The first expression would return true when run in the interp command, the second false.

Addition Expressions:
	Addition expressions use a '+' between two expressions.
	
	43 + 6
	x + 6
When run in using interp, the first would return 49, and the second would give an error as it has a free variable.
	

Multiplication Expressions:
	Multiplication expressions follow the same format as Addition expressions.
	
	11 * 5
	11 * x
As with the addition expressions, the first example would return 55, and the second example would return an error due to the free variable.
	
	
Variable Expressions:
	Similar to number expressions, variable expressions hold a single character or string.  Except '\_', all characters are valid, which is reserved within MSDScript for keywords like '_true'.
	
	x
	data
Both of the above would be valid MSDScript Variable Expressions.

Function Expressions:
	Function expressions and call expressions are related expressions.  A function expression is declared with a variable and an expression. for example
	
	_fun(x)(x + 5)

Let Expressions:
	They are similar to function expressions and allow MSDScript to solve algebraic equations. Let expressions always begin with the keyword '_let' followed by a variable, an equals sign, and then an expression. Let expressions are finished with the '\_in' keyword followed by an expression.  for example:
	
	_let x = 5 _in x+5
This expression would return the value 10 if run with interp.

If Expressions (If/Then statements):
	If expressions follow two different paths depending on if an initial expression evaluates to true or false. If the expression evaluates to true, it will follow the then expression. If false, it will follow the else expression.  As can be seen above, the grammar for this is the '_if' keyword followed by an expression, the '\_then' keyword followed by an expression, and finally, the '\_else' keyword followed by an expression. See the example below.
	
	_if 1==2 _then 2 _else 2+2
	
This expression would return the value 4 if run with interp.

	
Call Expression:
	Call expressions are similar to function expressions, except they include the argument to be called in the function.  Using the example from function expressions, valid call expressions would be as follows.
	
	(_fun (x) (x+1))(2 * 3)
	
If passed through the interp command, this expression would return the value 7.

###API

The following MSDScript functions can be used in your programs.

parse_str(String expr) - The parse_str function takes in an expression in the form of a string.  parse_str will then convert that string into the Expr class and return that class.

Step::interp_by_steps(Expr x) - The interp function will take in an Expr and return the solution to that Expr.

to_string() - the to_string function will convert an Expr class into a string for ease of use. An example of using MSDScript in a program is as follows.

	std::string expr = "_let factrl = _fun (factrl)
                _fun (x)
                  _if x == 1
                  _then 1
                  _else x * factrl(factrl)(x + -1)
	_in  factrl(factrl)(10)";  
	
	std::cout<<Step::interp_by_steps(parse_str(expr))->to_string();  	                                                                                                                                                                                            
This code will compute the factorial of 10 and print it out to the consol.  you coul instead save the value to a string to be used later in your program.

###Bug reporting
The software is offered as-is, and no support is provided.
###Licensing
This software is licensed under the MIT permissive software license.