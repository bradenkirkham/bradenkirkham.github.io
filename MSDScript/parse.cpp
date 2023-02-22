//
// Created by Braden Kirkham on 2/4/22.
//
#include "Expr.hpp"
#include <istream>
#include <sstream>
#include <iostream>
#include "parse.h"
#include "val.h"
#include "Env.h"
#include "Cont.h"
#include "Step.h"
#include "catch.hpp"


 PTR(Expr) parse(std::istream &inputStream){
    PTR(Expr) inputExpr = parse_expr(inputStream); // start the whole thing off with parse_expr

    skip_whitespace(inputStream); // skip whitespace
    if (!inputStream.eof()){ // we are assuming we will see an eof character.
        throw std::runtime_error("unexpected input after expression"); //error if not.
    }
    return inputExpr;
}

static PTR(Expr) parse_expr(std::istream &inputStream){
    skip_whitespace(inputStream); //skip to first start of expression.
    PTR(Expr) inputExpr;
    inputExpr = parse_comparg(inputStream); //check if we need to parse anything more specific.

    skip_whitespace(inputStream); // once that is parsed skip to the next thing.
    int c = inputStream.peek(); // check the next char.
    if (c == '='){ // we have to check for and consume == this is the first.
        consume(inputStream, '=');
        int d = inputStream.peek();
        PTR(Expr) rhs;
        if (d == '=') { // this is the second.
            consume(inputStream, '=');
            rhs = parse_expr(inputStream); // call parse expr on rhs.
        }
        return NEW(EqExpr)(inputExpr, rhs); // if we have an equal expr than this is it and we return it.
    }
    else{
        return inputExpr; // return regular expr if no equals.
    }
}

static PTR(Expr) parse_comparg(std::istream &inputStream){ // similar to above.
    skip_whitespace(inputStream);
    PTR(Expr) inputExpr;
    inputExpr = parse_addend(inputStream); //parse the lhs of whatever might be there.

    skip_whitespace(inputStream);
    int c = inputStream.peek();
    if (c == '+'){ // check for addition.
        consume(inputStream, '+');
        PTR(Expr) rhs = parse_comparg(inputStream);
        return NEW(AddExpr)(inputExpr, rhs); // return an add expr.
    }
    else{
        return inputExpr; // return regular expr if no +
    }
}

static PTR(Expr) parse_addend(std::istream &inputStream){ // same as comparg but checks for a * instead
    skip_whitespace(inputStream);
    PTR(Expr) inputExpr;
    inputExpr = parse_mult(inputStream); // parse lhs/expr.

    skip_whitespace(inputStream);
    int c = inputStream.peek();
    if (c == '*'){
        consume(inputStream, '*');
        PTR(Expr) rhs = parse_addend(inputStream);
        return NEW(MultExpr)(inputExpr, rhs);
    }
    else{
        return inputExpr;
    }
}

static PTR(Expr) parse_mult(std::istream &inputStream){
    PTR(Expr) newExpr = parse_inner(inputStream); // checks for basecase ('inner')
    while (inputStream.peek() == '('){ // if we get to this point and there are still parenthesis then it is a function?
        consume(inputStream, '(');
        PTR(Expr) actual_arg = parse_expr(inputStream); //parse what is in the parenthesis
        consume(inputStream, ')');
        newExpr = NEW(CallExpr)(newExpr, actual_arg); // return a callExpr

    }
    return newExpr; // return regular expression.
}

static PTR(Expr) parse_inner(std::istream &inputStream){
    skip_whitespace(inputStream);

    int getNext = inputStream.peek();
    if ((getNext == '-') || (isdigit(getNext))){ //if its a num
        return parse_num(inputStream);
    }
    else if (getNext == '('){ // if its another expr inside parenthesis.
        consume(inputStream, '(');
        PTR(Expr) inputExpr = parse_expr(inputStream);
        skip_whitespace(inputStream);
        getNext = inputStream.get();
        if (getNext != ')'){
            throw std::runtime_error("missing close parenthesis"); //make sure we get the closing parenthesis
        }
        return inputExpr;
    }
    else if(isalpha(getNext)){ // if it is a string/char.
        return parse_var(inputStream);
    }
    else if (getNext == '_'){ //if it is an underscore, parse keywords in the following way.
        consume(inputStream, '_'); //consume the underscore since parse_var only handles letters
        std::string newKeyword = (parse_var(inputStream))->to_string(); // use parse var to turn keyword into string
//        std::cout <<"new key word is " << newKeyword << "\n";

        if (newKeyword == "let"){ // if the keyword is let
            return parse_let(inputStream);
        }
        else if (newKeyword == "if"){ // keyword is if
            return parse_If(inputStream);
        }
        else if (newKeyword == "true"){ // keyword is true
            return NEW(BoolExpr)(true); // simply return a BoolExpr(true)
        }
        else if (newKeyword == "false"){ // keyword is false
            return NEW(BoolExpr)(false);
        }
        else if (newKeyword == "fun"){ // keyword indicates function.
            return parse_fun(inputStream);
        }
        else{
            throw std::runtime_error("malformed expression"); //if its not one of the standard keywords
        }
//        return parseUnderscore(inputStream);

//        getNext = inputStream.peek();
//        if (getNext == 'l') {
//            return parse_let(inputStream);
//        }
//        else if (getNext == 'i'){
//            return parse_If(inputStream);
//        }
    }
    else{
        consume(inputStream, getNext);
        throw std::runtime_error("invalid input");
    }
}

static PTR(Expr) parse_num(std::istream &inputStream){
    long accumulator = 0; // add number up
    bool isNegative = false; //helps track sign

    if (inputStream.peek() == '-'){ // tracks the sign of the num
        isNegative = true;
        consume(inputStream, '-');
        if(!(isdigit(inputStream.peek()))){
            throw std::runtime_error("invalid input");
        }
    }
    while (1){ // checks the next position in the stream and adds it to the accumulator if it is an int.
        int nextInt = inputStream.peek();
        if(isdigit(nextInt)){
            consume(inputStream, nextInt);
            accumulator = (accumulator*10) + (nextInt - '0');// fun math to turn a multiple digit number into the right number from a string.
//            std::cout << accumulator;

            if (accumulator > INT_MAX){
                throw std::runtime_error("Integer overflow");
            }
            if (accumulator < INT_MIN){
                throw std::runtime_error("Integer overflow");
            }
        }
        else{
            break; // breaks once we no longer see numbers
        }
    }
    if (isNegative){ //restores the sign of the num
        accumulator = -accumulator;
    }
    return NEW(NumExpr)(accumulator);
}

static PTR(Expr) parse_let(std::istream &inputStream){ // goes step by step and parses the function, checking for key words and parsing the expr following it.
    //parse_keyWord(inputStream, "let", false);

    skip_whitespace(inputStream);

     PTR(VarExpr) newLHS = CAST(VarExpr) (parse_var(inputStream)); //the lhs of a let is a var

    skip_whitespace(inputStream);

    parse_keyWord(inputStream, "=", false); //check for the equals and consume it as well.

    skip_whitespace(inputStream);

    PTR(Expr) newRHS = parse_expr(inputStream); // parse the expr for the rhs.

    skip_whitespace(inputStream);

    parse_keyWord(inputStream, "_in", false); //check for keyword _in and consume it.

    PTR(Expr) newBody = parse_expr(inputStream); // parse the new expr for the body.

    PTR(Expr) newLet = NEW(letExpr)(newLHS, newRHS, newBody); // construct NEW(letExpr)
    return newLet;

}

static PTR(Expr) parse_If(std::istream &inputStream){ // try turning parse_if and parse_let into for or while loops.
   // parse_keyWord(inputStream, "if", true);

    skip_whitespace(inputStream);

    PTR(Expr) newIf = parse_expr(inputStream); //parse the expr that is the if.

    skip_whitespace(inputStream);

    parse_keyWord(inputStream, "_then", true); // check for and consume keyword

    skip_whitespace(inputStream);

    PTR(Expr) newThen = parse_expr(inputStream); // parse then

    skip_whitespace(inputStream);

    parse_keyWord(inputStream, "_else", true); // check for and consume keyword

    skip_whitespace(inputStream);

    PTR(Expr) newElse = parse_expr(inputStream); // parse else

    return NEW(IfExpr)(newIf, newThen, newElse); // construct and return NEW(IfExpr).
}

static PTR(Expr) parse_fun(std::istream &inputstream){
    skip_whitespace(inputstream);

    char c = inputstream.peek();
    if (c == '('){ // consume parens if needed
        consume(inputstream, '(');
    }
    skip_whitespace(inputstream);

    std::string argString = (( CAST(VarExpr) )(parse_var(inputstream)))->to_string(); //string for arguement of function

    skip_whitespace(inputstream);
    c = inputstream.peek();
    if (c == ')'){
        consume(inputstream, ')'); // consume closing parens
    }
    skip_whitespace(inputstream);

    PTR(Expr) newBody = parse_expr(inputstream); // parse the body of the function.

    return NEW(FunExpr)(argString, newBody); // construct and return NEW(FunExpr)

}
static void *parse_keyWord(std::istream &inputStream, std::string keyword, bool isIf){ //call parse var to get a string and go from there.
    char varName; // tracks each character as it goes along.

    for (int i = 0; i < keyword.length(); ++i) { // goes through each char in the keyword
        varName = inputStream.get(); // consume the next char.
        if (varName != keyword[i]){ // if it doesn't match the keyword
            if (isIf){ //throw errors based on if it is an if or let expression.
                throw std::runtime_error("malformed if expression");
            }
            else {
                throw std::runtime_error("malformed let expression");
            }
        }
    }
    return 0;
}

//static  PTR(Expr)  parseUnderscore(std::istream &inputStream){
//    std::string keyWord = (parse_var(inputStream))->to_string();
////    std::cout << keyWord << "\n";
//    if (keyWord == "let"){
//        return parse_let(inputStream);
//    }
//    if (keyWord == "if"){
//        return parse_If(inputStream);
//    }
//    if (keyWord == "true"){
//        return NEW(BoolExpr)(true);
//    }
//    if (keyWord == "false"){
//        return NEW(BoolExpr)(false);
//    }
//    else{
//        throw std::runtime_error("malformed expression");
//    }
//}

static PTR(Expr) parse_var(std::istream &inputStream){
    std::string varName = "";

    while (1){
        char nextchar = inputStream.peek();
        if(isalpha(nextchar)){ // as long as next char is a letter
            consume(inputStream, nextchar); // consume it
            varName += nextchar; // store it in the string.
        }
        else{
            break; // break if it isn't a char.
        }
    }

    return NEW(VarExpr)(varName); // return the string.
}

PTR(Expr) parse_str(std::string s){
    std::stringstream ss(s); // helper function that uses a string for parsing instead of input stream.
    return parse(ss);
}

static void consume(std::istream &in, int expected) { // helper function to consume next part of stream
    int c = in.get(); // get next character(this consumes it)
    if (c != expected) { // if it is not right throw error.
        throw std::runtime_error("consume mismatch");
    }
}

void skip_whitespace(std::istream &inputStream){ // helper function to ignore spaces.
    while(1){
        int getNext = inputStream.peek(); // check the next char.
        if (!isspace(getNext)){ // if it isn't a space.
            break; // leave function
        }
        consume(inputStream, getNext); // otherwise consume it.
    }
}

TEST_CASE("parse") {
    CHECK_THROWS_WITH( parse_str("()"), "invalid input" );

    CHECK( parse_str("(1)")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("(((1)))")->equals(NEW(NumExpr)(1)) );

    CHECK_THROWS_WITH( parse_str("(1"), "missing close parenthesis" );

    CHECK( parse_str("1")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("10")->equals(NEW(NumExpr)(10)) );
    CHECK( parse_str("-3")->equals(NEW(NumExpr)(-3)) );
    CHECK( parse_str("  \n 5  ")->equals(NEW(NumExpr)(5)) );
    CHECK_THROWS_WITH( parse_str("-"), "invalid input" );
    // This was some temporary debugging code:
    //  std::istringstream in("-");
    //  parse_num(in)->print(std::cout); std::cout << "\n";

    CHECK_THROWS_WITH( parse_str(" -   5  "), "invalid input" );

    CHECK( parse_str("x")->equals(NEW(VarExpr)("x")) );
    CHECK( parse_str("xyz")->equals(NEW(VarExpr)("xyz")) );
    CHECK( parse_str("xYz")->equals(NEW(VarExpr)("xYz")) );
    CHECK_THROWS_WITH( parse_str("x_z"), "unexpected input after expression" );

    CHECK( parse_str("x + y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );
    CHECK( parse_str("x * y")->equals(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );
    CHECK( parse_str("z * x + y")
                   ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("z"), NEW(VarExpr)("x")),
                                        NEW(VarExpr)("y"))) );

    CHECK( parse_str("z * (x + y)")
                   ->equals(NEW(MultExpr)(NEW(VarExpr)("z"),
                                         NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) ));
}

//tests from Kellen
TEST_CASE("parse errors") {
    CHECK_THROWS_WITH( parse_str(""), "invalid input");
    CHECK_THROWS_WITH( parse_str("()"), "invalid input" );
    CHECK_THROWS_WITH( parse_str("(1"), "missing close parenthesis" );
    CHECK_THROWS_WITH( parse_str("-"), "invalid input" );
    CHECK_THROWS_WITH( parse_str(" -   5  "), "invalid input" );
    CHECK_THROWS_WITH(parse_str("0 + "), "invalid input");
    CHECK_THROWS_WITH(parse_str("0        ++"), "invalid input");
    CHECK_THROWS_WITH(parse_str("*t"), "invalid input");
    CHECK_THROWS_WITH( parse_str("x_z"), "unexpected input after expression" );
    CHECK_THROWS_WITH( parse_str("x Y"), "unexpected input after expression" );
    CHECK_THROWS_WITH( parse_str("_leet x = 5 _in 1"), "malformed expression");
    CHECK_THROWS_WITH( parse_str("_let x 5 _in 1"), "malformed let expression");
    CHECK_THROWS_WITH( parse_str("_let x = 5 _on 1"), "malformed let expression");
    std::stringstream test("test");
    CHECK_THROWS_WITH(consume(test, 'x'), "consume mismatch");
}

TEST_CASE("parse Nums") {
    CHECK( parse_str("1")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("1234")->equals(NEW(NumExpr)(1234)) );
    CHECK( parse_str("-3")->equals(NEW(NumExpr)(-3)) );
    CHECK( parse_str("  \n 5  ")->equals(NEW(NumExpr)(5)) );
    CHECK( parse_str("(1)")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("(((1)))")->equals(NEW(NumExpr)(1)) );
}

TEST_CASE("parse AddExpr") {
    CHECK( parse_str("1 + 2")->equals(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))) );
    CHECK( parse_str("\tx+y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );
    CHECK( parse_str("(-99 + a) + (2 + 2)")
                   ->equals(NEW(AddExpr)(NEW(AddExpr)(NEW(NumExpr)(-99), NEW(VarExpr)("a")), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(2)))));
}

TEST_CASE("parse MultExpr") {
    CHECK( parse_str("1     *         2")->equals(NEW(MultExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))) );
    CHECK( parse_str("x * y")->equals(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );
    CHECK( parse_str("(0*12345)\n*a")->equals(NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(0), NEW(NumExpr)(12345)), NEW(VarExpr)("a"))) );
}

TEST_CASE("parse VarExpr") {
    CHECK( parse_str("x")->equals(NEW(VarExpr)("x")) );
    CHECK( parse_str("xyz")->equals(NEW(VarExpr)("xyz")) );
    CHECK( parse_str("xYZ")->equals(NEW(VarExpr)("xYZ")) );
}

TEST_CASE("parse _let") {
    CHECK( parse_str("  _let  x  =  5  _in  x  +  1")->equals(NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))) );
    CHECK_THROWS_WITH( parse_str("_letx=5_in(_let y = 3_iny+2)+x"), "malformed expression");

}

TEST_CASE("parse combined") {
    CHECK( parse_str("z * x + y")
                   ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("z"), NEW(VarExpr)("x")),
                                        NEW(VarExpr)("y"))) );

    CHECK( parse_str("z * (x + y)")
                   ->equals(NEW(MultExpr)(NEW(VarExpr)("z"),
                                         NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) ));
    //add more to these
}

TEST_CASE("parse if"){
    CHECK(parse_str(" _if 3 == 3 _then 55 _else 88")->equals(NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(3), NEW(NumExpr)(3)), NEW(NumExpr)(55), NEW(NumExpr)(88))));
    CHECK_THROWS_WITH(parse_str(" _if 3 == 3 _the 55 _else 88"), "malformed if expression");

}

TEST_CASE("parse Eq"){
    CHECK(parse_str(" 40 + 5 == 38 + 7")->equals(NEW(EqExpr)((NEW(AddExpr)(NEW(NumExpr)(40), NEW(NumExpr)(5))), (NEW(AddExpr)(NEW(NumExpr)(38), NEW(NumExpr)(7))))));
}

TEST_CASE("parse Bool"){
    CHECK(parse_str("_true")->equals(NEW(BoolExpr)(true)));
    CHECK(parse_str("_false")->equals(NEW(BoolExpr)(false)));

}

TEST_CASE("parse Fun"){
    CHECK(parse_str("_fun (x) (x + 1)")->equals(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));

    CHECK(parse_str("(_fun (x) (x+1))(2 * 3)")->equals(NEW(CallExpr)(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))), NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))));
}



TEST_CASE("Niemann Tests"){
    std::string ifTest1 = "_let same = 1 == 2\n"
                     "_in  _if 1 == 2\n"
                     "     _then _false + 5\n"
                     "     _else 88";
    CHECK(parse_str(ifTest1)->interp(Env::empty)->to_string() == "88");

    CHECK( parse_str("1 + 2")->interp(Env::empty)->to_string() == "3" );

    std::string ifTest2 = "_if 4 + 1\n"
                     "_then 2\n"
                     "_else 3";
    CHECK_THROWS_WITH(parse_str(ifTest2)->interp(Env::empty)->to_string(), "from numVal: is_true function requires type bool");

    std::string ifTest3 = "_if 4 + 1\n"
                     "_thn 2\n"
                     "_else 3";
    CHECK_THROWS_WITH(parse_str(ifTest3)->interp(Env::empty)->to_string(), "malformed if expression");

    std::string ifTest4 = "_if 4 + 1\n"
                     "_then 2\n"
                     "_else3";
    CHECK_THROWS_WITH(parse_str(ifTest4)->interp(Env::empty)->to_string(), "from numVal: is_true function requires type bool");

    CHECK( (NEW(IfExpr)(NEW(BoolExpr)(true),
                       NEW(NumExpr)(1),
                       NEW(NumExpr)(2)))->interp(Env::empty)
                   ->equals(NEW(numVal)(1)) );

    CHECK( (NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)))->interp(Env::empty)
                   ->equals(NEW(numVal)(3)) );

    CHECK( parse_str("1 == 2")->interp(Env::empty)->to_string() == "_false" );
    CHECK_THROWS_WITH( parse_str("1 = 2")->interp(Env::empty)->to_string(), "unexpected input after expression");

    CHECK(parse_str("_true")->interp(Env::empty)->to_string() == "_true");
    CHECK(parse_str("_false")->interp(Env::empty)->to_string() == "_false");

    std::string ifTest5 = "_if _false\n"
                     "_then 2\n"
                     "_ele 3";
    CHECK_THROWS_WITH(parse_str(ifTest5)->interp(Env::empty)->to_string(), "malformed if expression");

    CHECK( parse_str("  _if  _true  _then  5 _else 6")->equals(NEW(IfExpr)(NEW(BoolExpr)(true), NEW(NumExpr)(5), NEW(NumExpr)(6))));
}

TEST_CASE("Functions"){
    CHECK((parse_str("_let factrl = _fun (factrl)\n"
                     "                _fun (x)\n"
                     "                  _if x == 1\n"
                     "                  _then 1\n"
                     "                  _else x * factrl(factrl)(x + -1)\n"
                     "_in  factrl(factrl)(10)"))
                  ->interp(Env::empty)->equals(NEW(numVal)(3628800)));
}

TEST_CASE("Continueations"){
    CHECK((Step::interp_by_steps(parse_str("_let countdown = _fun(countdown)\n"
                                           "                   _fun(n)\n"
                                           "                     _if n == 0\n"
                                           "                     _then 0\n"
                                           "                     _else countdown(countdown)(n + -1)\n"
                                           "_in countdown(countdown)(1000000)")))->to_string() == "0");

    CHECK( Step::interp_by_steps(parse_str("1"))->equals(NEW(numVal)(1)) );
    CHECK( Step::interp_by_steps(parse_str("_true"))->equals(NEW(BoolVal)(true)) );
    std::string ifTest1 = "_let same = 1 == 2\n"
                          "_in  _if 1 == 2\n"
                          "     _then _false + 5\n"
                          "     _else 88";
    CHECK((Step::interp_by_steps(parse_str(ifTest1))->to_string() == "88"));
    CHECK((Step::interp_by_steps(parse_str("2 * 3"))->to_string() == "6"));
    PTR(val) val = NEW(numVal)(6);
    PTR(BoolVal) b = NEW(BoolVal)(true);
    CHECK_THROWS_WITH(b->call_step(val, NEW(DoneCont)()), "call_step of non-FunVal");
    CHECK_THROWS_WITH(val->call_step(val, NEW(DoneCont)()), "call_step of non-FunVal");
    PTR(Cont) cont = NEW(DoneCont)();
    CHECK_THROWS_WITH(cont->step_continue(), "Cannot step_continue when done");
}