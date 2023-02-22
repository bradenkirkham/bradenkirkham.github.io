#include "Expr.hpp"
#include "catch.hpp"
#include "val.h"
#include <string>
#include <stdexcept>
#include <sstream>
#include <iostream>
#include "Env.h"
#include "Step.h"
#include "Cont.h"

//Expr utility functions
std::string Expr::to_string (){
    std::stringstream output(""); //declares outputstream
    this->print(output); // writes to outputstream
    return output.str(); // converts outputstream to str and returns
}

std::string Expr::to_string_pretty(){ // uses pretty print to return a string... a bit redundant.
    return this->pretty_print();
}

//void Expr::pretty_print(std::ostream& output){
//    std::streampos streampos = output.tellp();
//    return this->pretty_print_at(output, prec_none, false, streampos);
//}

std::string Expr::pretty_print() { // front facing function for pretty_print_at
    std::stringstream stringStream("");
    std::streampos start = 0; //start streampos at 0
    this->pretty_print_at(stringStream, prec_none, false, start); //call pretty print at
    return stringStream.str(); //return stringstream converted to string.

}

//NumExpr function defintions
NumExpr::NumExpr(int rep) { //constructor for numExpr.  numExpr holds an int.
    this->rep = rep;
}

bool NumExpr::equals( PTR(Expr) e) { // compares two num expr.
     PTR(NumExpr) compr = CAST(NumExpr)(e); // cast the expression to numexpr
    if (compr == nullptr) { // if the expression is not a numexpr
        return false;
    }
    else{ // base case for equals
        return (this->rep == compr->rep); // return the evalution of the member variables of the two numexprs.
    }
}

 PTR(val)  NumExpr::interp(PTR(Env) env){ // base case of interp, returns the member variable for numexpr
    return NEW(numVal)(rep);
}

void NumExpr::step_interp() {
    Step::mode = Step::continue_mode;
    Step::val = NEW(numVal)(rep);
    Step::cont = Step::cont; /* no-op */
}

//bool NumExpr::has_variable(){
//    return false;
//}

// PTR(Expr)  NumExpr::subst(std::string var, PTR(Expr) e){ // subst should not change Nums.
//    return THIS;
//}

void NumExpr::print(std::ostream& output){ //simple print helper function
    output << this->rep;
}

void NumExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos){ //pretty print function
    output << this->rep;
}

//BoolExpr function definitions

BoolExpr::BoolExpr(bool rep) { //constructor, takes a bool.
    this->rep = rep;
}

bool BoolExpr::equals( PTR(Expr) e) {//equals works the same as for numexpr
     PTR(BoolExpr) compr = CAST(BoolExpr)(e);
    if (compr == nullptr) {
        return false;
    } else { // base case for equals
        return (this->rep == compr->rep);
    }
}

 PTR(val)  BoolExpr::interp(PTR(Env) env) {
    return NEW(BoolVal)(rep);
}

void BoolExpr::step_interp(){
    Step::mode = Step::continue_mode;
    Step::val = NEW(BoolVal)(rep);
    Step::cont = Step::cont; /* no-op */
}

//bool BoolExpr::has_variable() {
//    return false;
//}

// PTR(Expr)  BoolExpr::subst(std::string var, PTR(Expr) e) {
//    return THIS;
//}

void BoolExpr::print(std::ostream& output) {
    if (this->rep == true) {
        output << "_true";
    }
    else{
        output << "_false";
    }
}

void BoolExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) {
    if (this->rep == true) {
        output << "_true";
    }
    else{
        output << "_false";
    }
}

//EqExpr functions

EqExpr::EqExpr( PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool EqExpr::equals( PTR(Expr) e) {
     PTR(EqExpr) compr = CAST(EqExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // this had to be changed to a recursive call to work properly
        return (((this->lhs)->equals(compr->lhs)) && ((this->rhs)->equals(compr->rhs))); //obviously works the same as num expr but for expr with multiple member variables we need to check all for equivalency
    }
}

 PTR(val) EqExpr::interp(PTR(Env) env) {
    return NEW(BoolVal)((rhs->interp(env)->equals(lhs->interp(env)))); //interping an equals expression returns a boolean.
}

void EqExpr::step_interp(){
    Step::mode = Step::interp_mode;
    Step::expr = lhs;
    Step::env = Step::env; /* no-op, so could omit */
    Step::cont = NEW(RightThenEqCont)(rhs, Step::env, Step::cont);
}

//bool EqExpr::has_variable() {
//    return (rhs->has_variable() || lhs->has_variable());
//}

// PTR(Expr) EqExpr::subst(std::string var, PTR(Expr) e) { //subst checks both sides of the expr by calling subst on both sides.
//    return NEW(EqExpr)(lhs->subst(var, e), rhs->subst(var, e));
//}

void EqExpr::print(std::ostream &output) {
    output << "(";
    lhs->print(output);
    output << "==";
    rhs->print(output);
    output << ")";
}

void EqExpr::pretty_print_at(std::ostream &output, precedence_t prec, bool needsparens, std::streampos &streampos) {
    if (prec >= prec_equals) { //using precidence to tell us if this needs parenthesis or not.
        output << "(";
    }

    lhs->pretty_print_at(output, prec_equals, true, streampos);
    output << " == ";
    rhs->pretty_print_at(output, prec_none, false, streampos);

    if (prec >= prec_add) {
        output << ")";
    }
}

//AddExpr function definitions
AddExpr::AddExpr( PTR(Expr) lhs, PTR(Expr) rhs) {  //constructor for add expr, takes two expr.
    this->lhs = lhs;
    this->rhs = rhs;
}

bool AddExpr::equals( PTR(Expr) e) {
     PTR(AddExpr) compr = CAST(AddExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // this had to be changed to a recursive call to work properly
        return (((this->lhs)->equals(compr->lhs)) && ((this->rhs)->equals(compr->rhs)));
    }
}

 PTR(val)  AddExpr::interp(PTR(Env) env){ // recursive call ensures all expr are solved on rhs and lhs before returning the sum of both
    return (lhs->interp(env)->add_to(rhs->interp(env)));
}

void AddExpr::step_interp() {
    Step::mode = Step::interp_mode;
    Step::expr = lhs;
    Step::env = Step::env; /* no-op, so could omit */
    Step::cont = NEW(RightThenAddCont)(rhs, Step::env, Step::cont);
}

//bool AddExpr::has_variable(){ // recursive call returns true if VarExpr is found on lhs or rhs
//    return (lhs->has_variable() || rhs->has_variable());
//}

// PTR(Expr)  AddExpr::subst(std::string var, PTR(Expr) e){ // recursive call to be sure that all VarExpr are replaced
//    return NEW(AddExpr)(lhs->subst(var, e), rhs->subst(var, e));
//}

void AddExpr::print(std::ostream& output){
    output << "(";
    lhs->print(output);
    output << "+";
    rhs->print(output);
    output << ")";
}

void AddExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) {

    if (prec >= prec_add){
        output << "(";
    }

    lhs->pretty_print_at(output, prec_add, true, streampos);
    output << " + ";
    rhs->pretty_print_at(output, prec_equals, false, streampos);

    if (prec>= prec_add){
        output << ")";
    }

}

//MultExpr function defintions.
MultExpr::MultExpr( PTR(Expr) lhs, PTR(Expr) rhs) { // constructor for multExpr takes two expr.
    this->lhs = lhs;
    this->rhs = rhs;
}

bool MultExpr::equals( PTR(Expr) e) {
     PTR(MultExpr) compr = CAST(MultExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // this had to be changed to a recursive call to work properly.
        return (((this->lhs)->equals(compr->lhs)) && ((this->rhs)->equals(compr->rhs)));
    }
}

 PTR(val)  MultExpr::interp(PTR(Env) env){ // recursive call ensures that all expr are solved on rhs and lhs before returning the product of both
    return (lhs->interp(env)->mult_by(rhs->interp(env)));

}

void MultExpr::step_interp(){
    Step::mode = Step::interp_mode;
    Step::expr = lhs;
    Step::env = Step::env; /* no-op, so could omit */
    Step::cont = NEW(RightThenMultCont)(rhs, Step::env, Step::cont);
}

//bool MultExpr::has_variable(){
//    return (lhs->has_variable() || rhs->has_variable()); //recursive call, if either right or left returns true return true.
//}

// PTR(Expr)  MultExpr::subst(std::string var, PTR(Expr) e){ // recursive call till base case which is VarExpr
//    return NEW(MultExpr)(lhs->subst(var, e), rhs->subst(var, e));
//}

void MultExpr::print(std::ostream& output){

    output << "(";
    lhs->print(output);
    output << "*";
    rhs->print(output);
    output << ")";

}

void MultExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) {

    if (prec == prec_mult){
        output << "(";
    }

    lhs->pretty_print_at(output, prec_mult, true, streampos);
    output << " * ";
    rhs->pretty_print_at(output, prec_add, needsparens, streampos);

    if (prec == prec_mult){
        output << ")";
    }

}

//VarExpr function definitions.
VarExpr::VarExpr (std::string val){ // constructor for VarExpr, member variable holds a string
    this->rep = val;
}

bool VarExpr::equals( PTR(Expr) e){
     PTR(VarExpr) compr = CAST(VarExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // base case for equals
        return (this->rep == compr->rep);
    }
}

 PTR(val)  VarExpr::interp(PTR(Env) env){ // can't interp a var
    return env->lookup(this->rep);
//    throw std::runtime_error("ERROR: This function doesn't support VarExpr");
}

void VarExpr::step_interp(){
    Step::mode = Step::continue_mode;
    Step::val = Step::env->lookup(this->rep);
    Step::cont = Step::cont; /* no-op */
}

//bool VarExpr::has_variable(){
//    return true;
//}

// PTR(Expr)  VarExpr::subst(std::string var, PTR(Expr) e) { //base case for subst.
//    if (this->rep == var) { // comparison to check value is equal to the string you are replacing
//        return e; // if it is you return the expression that was passed in.
//    }
//    return (THIS); // otherwise to keep the var the same you return this.
//}

void VarExpr::print(std::ostream& output){
    output << this->rep;
}

void VarExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) {
    output << this->rep;
}


//letExpr function definitions
letExpr::letExpr( PTR(VarExpr) lhs, PTR(Expr) rhs, PTR(Expr) body) { // constructor for letExpr, more complex takes a lhs var, and expr for rhs, and body.
    this->lhs = lhs; // let lhs = rhs
    this->rhs = rhs;//      in body
    this->body = body;
}

bool letExpr::equals( PTR(Expr) e) {
     PTR(letExpr) compr = CAST(letExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // base case for equals
        return (rhs->equals(compr->rhs) && body->equals(compr->body));
    }
}

 PTR(val)  letExpr::interp(PTR(Env) env){ // recursive call ensures that all expr are solved on rhs and lhs before returning the product of both
     PTR(val) rhsInterp = rhs->interp(env); //interp rhs
    PTR(Env) newEnv = NEW(ExtendedEnv) (lhs->rep, rhsInterp, env);// body->subst(lhs->rep, rhsInterp->to_expr()); // subst any matching var in body w/rhsinterp
    return body->interp(newEnv); // return interp of new expression.
}

void letExpr::step_interp() {
    Step::mode = Step::interp_mode;
    Step::expr = rhs;
    Step::env = Step::env;
    Step::cont = NEW(LetBodyCont)(lhs->rep, body, Step::env, Step::cont);
}

//bool letExpr::has_variable(){
//    return (rhs->has_variable() || body->has_variable());
//}

// PTR(Expr)  letExpr::subst(std::string var, PTR(Expr) e){ // recursive call till base case which is VarExpr
//    PTR(Expr) newRHS = rhs->subst(var, e); // always subst rhs
//    PTR(Expr) newBody = body; //declared this variable for convenience.
//    if (lhs->rep != var){ // if the lhs is not the same as the subst var.
//        newBody = body->subst(var, e); //check the body and subst with var and capture that in the newbody var declared above.
//    }
//    return NEW(letExpr)(lhs, newRHS, newBody);//return a new let w/lhs, new rhs, and body.
//}

void letExpr::print(std::ostream& output){
    output << "(_let ";
    lhs->print(output);
    output << "=";
    rhs->print(output);
    output << " _in ";
    body->print(output);
    output << ")";
}

void letExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) {
    if(needsparens){
        output << "(";
    }
    int startPosition = output.tellp() - streampos; //position
    output << "_let ";
    output << lhs->rep << " = ";
    rhs->pretty_print_at(output, prec_none, false, streampos);

    output << "\n";
    streampos = output.tellp();
    for(int i = 0; i < startPosition; i++){
        output << " ";
    }
    output << "_in  ";
    body->pretty_print_at(output, prec_none, false, streampos);

    if (needsparens){
        output << ")";
    }
}

//IfExpr function definitions

IfExpr::IfExpr( PTR(Expr) _if, PTR(Expr) _then, PTR(Expr) _else) { // constructor for if, takes three expr.
    this->_if = _if;
    this->_then = _then;
    this->_else = _else;
}

bool IfExpr::equals( PTR(Expr) e) {
     PTR(IfExpr) compr = CAST(IfExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // base case for equals
        return (_if->equals(compr->_if) && _then->equals(compr->_then) && _else->equals(compr->_else));
    }
}

 PTR(val) IfExpr::interp(PTR(Env) env) {
    if (_if->interp(env)->is_true()){ //if returns varies based on if the if expr evaluates to true or false.
        return _then->interp(env); // if true return _then
    }
    else{
        return _else->interp(env); // if false return _else
    }
}

void IfExpr::step_interp() {
    Step::mode = Step::interp_mode;
    Step::expr = _if;
    Step::env = Step::env;
    Step::cont = NEW(IfBranchCont)(_then, _else, Step::env, Step::cont);
}

//bool IfExpr::has_variable() {
//    return (_if->has_variable() || _then->has_variable() || _else->has_variable());
//}

// PTR(Expr) IfExpr::subst(std::string var, PTR(Expr) e) {
//    return NEW(IfExpr)(_if->subst(var, e), _then->subst(var, e), _else->subst(var, e));
//}

void IfExpr::print(std::ostream &output) {
    output << "( _if ";
    _if->print(output);
    output << " ";
    output << "_then ";
    _then->print(output);
    output << " ";
    output << "_else ";
    _else->print(output);
    output << " )";
}

void IfExpr::pretty_print_at(std::ostream &output, precedence_t prec, bool needsparens, std::streampos &streampos) {
    if (needsparens){
        output << "(";
    }
    int startposition = output.tellp() - streampos;

    output << "_if ";
    _if->pretty_print_at(output, prec_none, false, streampos);

    output << "\n";
    streampos = output.tellp();
    for (int i = 0; i < startposition; ++i) {
        output << " ";
    }
    output << "_then ";
    _then->pretty_print_at(output, prec_none, false, streampos);

    output << "\n";
    for (int i = 0; i < startposition; ++i) {
        output << " ";
    }
    output << "_else ";
    _else->pretty_print_at(output, prec_none, false, streampos);

    if (needsparens){
        output << ")";
    }
}

//Fun_expr
FunExpr::FunExpr(std::string formal_arg, PTR(Expr) body){ //constructor for funExpr takes a string and a expr body.
    this->formal_arg = formal_arg;
    this->body = body;
}

bool FunExpr::equals( PTR(Expr) e){
     PTR(FunExpr) compr = CAST(FunExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // base case for equals
        return (formal_arg == (compr->formal_arg) && body->equals(compr->body));
    }
}

 PTR(val)  FunExpr::interp(PTR(Env) env){ //returns a funval.
    return NEW(FunVal)(formal_arg, body, env);
}

void FunExpr::step_interp(){
    Step::mode = Step::continue_mode;
    Step::val = NEW(FunVal)(formal_arg, body, Step::env);
    Step::cont = Step::cont; /* no-op */
}

// PTR(Expr)  FunExpr::subst(std::string var, PTR(Expr) e){ //similar to how we checked let for subst.
//    if (formal_arg == var){
//        return THIS;
//    }
//    else{
//        return NEW(FunExpr)(formal_arg, body->subst(var, e));
//    }
//}

void FunExpr::print(std::ostream& output){

    output << "(_fun ";
    output << "(";
    output << formal_arg;
    output << ") ";

    body->print(output);

    output << ")";

}

void FunExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos){
    this->print(output);
}

//Call_expr
CallExpr::CallExpr( PTR(Expr) to_be_called, PTR(Expr) actual_arg){ //constructor for callExpr takes two Expr.
    this->to_be_called = to_be_called;
    this->actual_arg = actual_arg;
}

bool CallExpr::equals( PTR(Expr) e){
     PTR(CallExpr) compr = CAST(CallExpr)(e);
    if (compr == nullptr){
        return false;
    }
    else{ // base case for equals
        return (actual_arg->equals(compr->actual_arg) && to_be_called->equals(compr->to_be_called));
    }
}

 PTR(val)  CallExpr::interp(PTR(Env) env){ //interp handled in val class
    return to_be_called->interp(env)->call(actual_arg->interp(env)); //one expr is the argument the other is the function the arguement is placed in i think.
}

void CallExpr::step_interp() {
    Step::mode = Step::interp_mode;
    Step::expr = to_be_called;
    Step::cont = NEW(ArgThenCallCont)(actual_arg, Step::env, Step::cont);
}

// PTR(Expr)  CallExpr::subst(std::string var, PTR(Expr) e){
//    return NEW(CallExpr)(to_be_called->subst(var, e), actual_arg->subst(var, e));
//}

void CallExpr::print(std::ostream& output){

//    output << "(";
    to_be_called->print(output);
//    output << ") ";

    output << "(";
    actual_arg->print(output);
    output << ")";

}

void CallExpr::pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos){
    this->print(output);
}



TEST_CASE("AddExpr"){
     PTR(NumExpr) testNum1 = NEW(NumExpr)(6);
     PTR(NumExpr) testNum2 = NEW(NumExpr)(6);
     PTR(NumExpr) testNum3 = NEW(NumExpr)(1);
     PTR(NumExpr) testNum4 = NEW(NumExpr)(3);

     PTR(AddExpr) testAdd1 = NEW(AddExpr)(testNum1, testNum2);
     PTR(AddExpr) testAdd2 = NEW(AddExpr)(testNum3, testNum4);
    CHECK(AddExpr(testNum1, testNum2).equals(testAdd1) == true); // testing true case
    CHECK(testAdd1->equals(testAdd2) == false); // testing false case
    CHECK(testAdd1->equals(NEW(VarExpr)("six")) == false); // testing different type (nullptr)

}

TEST_CASE("NumExpr"){
     PTR(NumExpr) testNum1 = NEW(NumExpr)(6);
     PTR(NumExpr) testNum2 = NEW(NumExpr)(6);

     PTR(NumExpr) testNum3 = NEW(NumExpr)(3);
     PTR(NumExpr) testNum4 = NEW(NumExpr)(45);

    CHECK(testNum1->equals(testNum2) == true); // testing true case
    CHECK(testNum3->equals(testNum4) == false); // testing false case
    CHECK(testNum4->equals(NEW(VarExpr)("test")) == false); // testing different type (nullptr)
}

TEST_CASE("MultExpr"){
     PTR(NumExpr) testNum1 = NEW(NumExpr)(6);
     PTR(NumExpr) testNum2 = NEW(NumExpr)(3);
     PTR(NumExpr) testNum3 = NEW(NumExpr)(45);


     PTR(MultExpr) testMult1 = NEW(MultExpr)(testNum3, testNum2);
     PTR(MultExpr) testMult2 = NEW(MultExpr)(testNum3, testNum2);
     PTR(MultExpr) testMult3 = NEW(MultExpr)(testNum1, testNum3);
    CHECK(testMult1->equals(testMult2) == true); // testing true case
    CHECK(testMult1->equals(testMult3) == false); // testing false case
    CHECK(testMult1->equals(NEW(NumExpr)(7)) == false); // testing different type
}

TEST_CASE("VarExpr"){
     PTR(VarExpr) testVar1 = NEW(VarExpr)("test string");
     PTR(VarExpr) testVar2 = NEW(VarExpr)("test string");
     PTR(VarExpr) testVar3 = NEW(VarExpr)("tst str");

    CHECK(testVar1->equals(testVar2) == true); // testing the true case
    CHECK(testVar1->equals(testVar3) == false); // testing false case
    CHECK(testVar1->equals(NEW(NumExpr)(7)) == false); // testing non strings

}

TEST_CASE("interp"){
     PTR(NumExpr) testNum1 = NEW(NumExpr) (7); // simple check that interp works with num.
    CHECK(testNum1->interp(Env::empty)->equals(NEW(numVal)(7)));

     PTR(AddExpr) testAdd1 = NEW(AddExpr) (NEW(NumExpr)(8), testNum1); // checking that interp works with add.
    CHECK(testAdd1->interp(Env::empty)->equals(NEW(numVal)(15)));

     PTR(MultExpr) testMult1 = NEW(MultExpr) (testAdd1, testNum1); // testing that interp works with linked mult
    CHECK(testMult1->interp(Env::empty)->equals(NEW(numVal)(105)));

     PTR(AddExpr) testAdd2 = NEW(AddExpr) (testMult1, testAdd1); // testing that interp works with linked add
    CHECK(testAdd2->interp(Env::empty)->equals(NEW(numVal)(120)));

     PTR(AddExpr) testAdd3 = NEW(AddExpr) (testNum1, NEW(NumExpr)(-4));
    CHECK(testAdd3->interp(Env::empty)->equals(NEW(numVal)(3)));

     PTR(MultExpr) testMult2 = NEW(MultExpr) (testMult1, NEW(NumExpr)(-2));
    CHECK(testMult2->interp(Env::empty)->equals(NEW(numVal)(-210)));

     PTR(VarExpr) testVar1 = NEW(VarExpr) ("a"); // testing that var throws error in interp
    CHECK_THROWS_WITH(testVar1->interp(Env::empty), "Free Variable: a");

     PTR(MultExpr) testMult3 = NEW(MultExpr) (testMult1, testVar1); // testing that error is thrown in expr for interp
    CHECK_THROWS_WITH(testMult3->interp(Env::empty), "Free Variable: a");
}

//TEST_CASE("has_variable"){
//     PTR(VarExpr) testVar1 = NEW(VarExpr)("a"); // simple true has_variable with single var
//    CHECK(testVar1->has_variable() == true);
//
//     PTR(AddExpr) testAdd1 = NEW(AddExpr)(NEW(NumExpr)(1), testVar1); //true has_variable in simple add
//    CHECK(testAdd1->has_variable() == true);
//
//     PTR(MultExpr) testMult1 = NEW(MultExpr)(testAdd1, testVar1); // true has_variable in simple mult
//    CHECK(testMult1->has_variable() == true);
//
//     PTR(NumExpr) testNum1 = NEW(NumExpr)(7); // testing that num returns false for has_variable
//    CHECK(testNum1->has_variable() == false);
//
//     PTR(AddExpr) testAdd2 = NEW(AddExpr)(testNum1, NEW(NumExpr)(1)); // testing that num returns false in add
//    CHECK(testAdd2->has_variable() == false);
//
//     PTR(MultExpr) testMult2 = NEW(MultExpr)(testAdd2, testNum1); //testing that num returns false in mult
//    CHECK(testMult2->has_variable() == false);
//
//     PTR(AddExpr) testAdd3 = NEW(AddExpr)(testAdd2, testMult1); // testing true has_variable in linked add
//    CHECK(testAdd3->has_variable() == true);
//
//     PTR(MultExpr) testMult3 = NEW(MultExpr) (testMult1, testAdd2); // testing true has_variable in linked mult
//    CHECK(testMult3->has_variable() == true);
//
//     PTR(AddExpr) testAdd4 = NEW(AddExpr)(testAdd2, testMult2); // testing false has_variable in linked add
//    CHECK(testAdd4->has_variable() == false);
//
//     PTR(MultExpr) testMult4 = NEW(MultExpr)(testMult2, testAdd2); // testing false has_variable in linked mult
//    CHECK(testMult4->has_variable() == false);

//}
//
//TEST_CASE("subst"){
//
//    CHECK(((NEW(VarExpr) ("x")))->subst("x", NEW(VarExpr)("y"))->equals(NEW(VarExpr)("y"))); //simple subst test with single var
//
//    CHECK( (NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
//                   ->subst("x", NEW(VarExpr)("y"))
//                   ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) ); // testing subst in add
//
//     PTR(VarExpr) testVar1 = NEW(VarExpr) ("x"); //test expressions to use in checks.
//     PTR(AddExpr) testAdd1 = NEW(AddExpr) (testVar1, NEW(NumExpr)(2));
//     PTR(MultExpr) testMult1 = NEW(MultExpr) (NEW(NumExpr)(2), testVar1);
//
//     PTR(MultExpr) testMult2 = CAST(MultExpr)(testMult1->subst("x", NEW(NumExpr)(3))); // testing subst in mult
//    CHECK(testMult2->interp(Env::empty)->equals(NEW(numVal)(6)));
//
//     PTR(AddExpr) testAdd2 = CAST(AddExpr)(testAdd1->subst("x", NEW(NumExpr)(3))); // testing subst in add
//    CHECK(testAdd2->interp(Env::empty)->equals(NEW(numVal)(5)));
//
//     PTR(MultExpr) testMult3 = NEW(MultExpr) (testMult1, testAdd2); // testing subst in linked mult
//    CHECK((testMult3->subst("x", NEW(NumExpr)(2)))->interp(Env::empty)->equals(NEW(numVal)(20)));
//
//     PTR(AddExpr) testAdd3 = NEW(AddExpr) (testAdd1, testMult2); // testing subst in linked add
//    CHECK((testAdd3->subst("x", NEW(NumExpr)(2)))->interp(Env::empty)->equals(NEW(numVal)(10)));
//
//}

TEST_CASE("print and pretty_print"){

     PTR(NumExpr) testNum1 = NEW(NumExpr)(3);

    CHECK(testNum1->to_string() == "3");

     PTR(VarExpr) testVar1 = NEW(VarExpr)("x");

    CHECK(testVar1->to_string() == "x");

     PTR(AddExpr) testAdd1 = NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2));

    CHECK(testAdd1->to_string() == "(1+2)");

     PTR(AddExpr) testAdd2 = NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3));

    CHECK(testAdd2->to_string() == "(2+3)");

     PTR(AddExpr) testAdd3 = NEW(AddExpr)(testAdd1, testNum1);

    CHECK(testAdd3->to_string() == "((1+2)+3)");

     PTR(AddExpr) testAdd4 = NEW(AddExpr)(NEW(NumExpr)(1), testAdd2);

    CHECK(testAdd4->to_string() == "(1+(2+3))");

     PTR(AddExpr) testAdd5 = NEW(AddExpr)(testNum1, testVar1);

    CHECK(testAdd5->to_string() == "(3+x)");

     PTR(AddExpr) testAdd6 = NEW(AddExpr)(testAdd5, NEW(VarExpr)("y"));

    CHECK(testAdd6->to_string() == "((3+x)+y)");

     PTR(MultExpr) testMult1 = NEW(MultExpr)(testNum1, testVar1);

    CHECK(testMult1->to_string() == "(3*x)");

     PTR(MultExpr) testMult2 = NEW(MultExpr)(testMult1, testAdd2);

    CHECK(testMult2->to_string() == "((3*x)*(2+3))");

     PTR(NumExpr) Num3 = NEW(NumExpr)(3);
     PTR(AddExpr) Add1 = NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2));
     PTR(AddExpr) Add3 = NEW(AddExpr)(Num3, Add1);
     PTR(AddExpr) Add4 = NEW(AddExpr)(Add1, Num3);

    CHECK(Add3->pretty_print() == "3 + 1 + 2");

    CHECK(Add4->pretty_print() == "(1 + 2) + 3");

    CHECK(testMult2->pretty_print() == "(3 * x) * (2 + 3)");

    CHECK(testMult1->pretty_print() == "3 * x");

    CHECK(testAdd5->pretty_print() == "3 + x");

    CHECK(testAdd6->pretty_print() == "(3 + x) + y");


}

TEST_CASE("_let prettyprint"){
    //tests from Jason
    //Let Pretty Print Problem 1
     PTR(AddExpr) add11 = NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2));
     PTR(letExpr) let11 = NEW(letExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(3), add11);
     PTR(AddExpr) add12 = NEW(AddExpr)(let11, NEW(VarExpr)("x"));
     PTR(letExpr) let12 = NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), add12);
//    cout << let12->to_string() << "\n";

    //Solution 1
    std::string result11 = "_let x = 5\n"
                      "_in  (_let y = 3\n"
                      "      _in  y + 2) + x";
//    cout << result11 << "\n";

    //Check 1
//    CHECK((let12->to_prettyString() == result11));


    //Let Pretty Print Problem 2
     PTR(letExpr) let21 = NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(VarExpr)("x"));
     PTR(MultExpr) mult21 = NEW(MultExpr)(NEW(NumExpr)(5), let21);
     PTR(AddExpr) add21 = NEW(AddExpr)(mult21, NEW(NumExpr)(1));
//    cout << add21->interp();

    //Solution 2
    std::string result21 = "5 * (_let x = 5\n"
                      "     _in  x) + 1";
//    cout << result21 << "\n";

    //Check 2
//    CHECK((add21->to_prettyString() == result21));

//Tests from Kellen

    CHECK((NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))))->pretty_print() == "_let x = 5\n"
                       "_in  x + 1");

    CHECK((NEW(letExpr)(NEW(VarExpr)("xx"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("xx"), NEW(NumExpr)(1))))->pretty_print() == "_let xx = 5\n"
                       "_in  xx + 1");


    CHECK((NEW(AddExpr)(NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(VarExpr)("x")), NEW(NumExpr)(1)))->pretty_print() == "(_let x = 5\n"
                       " _in  x) + 1");

    CHECK((NEW(MultExpr)(NEW(NumExpr)(5), NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))))->pretty_print() == "5 * _let x = 5\n"
                       "    _in  x + 1");

    CHECK((NEW(AddExpr)(NEW(MultExpr)(NEW(NumExpr)(5), NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(VarExpr)("x"))), NEW(NumExpr)(1)))->pretty_print() == "5 * (_let x = 5\n"
                       "     _in  x) + 1");

    CHECK((NEW(letExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(letExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))), NEW(VarExpr)("x"))))->pretty_print() == "_let x = 5\n"
                       "_in  (_let y = 3\n"
                       "      _in  y + 2) + x");

    CHECK((NEW(letExpr)(NEW(VarExpr)("x"), NEW(letExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(6), NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))))->pretty_print() == "_let x = _let y = 6\n"
                       "         _in  y * 2\n"
                       "_in  x + 1");
}

//TEST_CASE("_let subst"){
//    //tests developed with Jason
//    //Bound Variables in the body are not substituted
//     PTR(letExpr) let1 = NEW(letExpr)(NEW(VarExpr)("A"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("A")));
//     PTR(letExpr) let2 = CAST(letExpr)(let1->subst("A", NEW(VarExpr)("C")));
//     PTR(letExpr) let3 = NEW(letExpr)(NEW(VarExpr)("A"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("C")));
////    cout << let3->to_string() << "\n";
//    CHECK( ( let2->equals(let3)) == false);
//
//    //Example of variables in the body not being bound
//     PTR(letExpr) let4 = NEW(letExpr)(NEW(VarExpr)("A"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("B")));
//     PTR(letExpr) let5 = CAST(letExpr)(let4->subst("B", NEW(VarExpr)("C")));
//     PTR(letExpr) let6 = NEW(letExpr)(NEW(VarExpr)("A"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("C")));
//    CHECK( ( let5->equals(let6)) );
//
//    //Variables in the rhs are NEVER bound and therefore can be substituted
//     PTR(letExpr) let7 = NEW(letExpr)(NEW(VarExpr)("A"), NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("A")), NEW(NumExpr)(5));
//     PTR(letExpr) let8 = CAST(letExpr)(let7->subst("A", NEW(VarExpr)("C")));
//     PTR(letExpr) let9 = NEW(letExpr)(NEW(VarExpr)("A"), NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("C")), NEW(NumExpr)(5));
//    CHECK( ( let8->equals(let9)) );
//}

//TEST_CASE("_let hasvariable"){
//     PTR(VarExpr) nameVar = NEW(VarExpr)("X");
//     PTR(NumExpr) testNum1 = NEW(NumExpr)(5);
//     PTR(AddExpr) testAdd1 = NEW(AddExpr)(nameVar, testNum1);
//     PTR(letExpr) test1 = NEW(letExpr)(nameVar, testNum1, testAdd1);
//    CHECK(test1->has_variable()==true);
//}

TEST_CASE("_let interp"){
     PTR(VarExpr) nameVar = NEW(VarExpr)("X");
     PTR(NumExpr) testNum1 = NEW(NumExpr)(5);
     PTR(AddExpr) testAdd1 = NEW(AddExpr)(nameVar, testNum1);
     PTR(letExpr) test1 = NEW(letExpr)(nameVar, testNum1, testAdd1);
    CHECK(test1->interp(Env::empty)->equals(NEW(numVal)(10)));

     PTR(letExpr) test2 = NEW(letExpr)(nameVar, nameVar, testAdd1);
    CHECK_THROWS_WITH(test2->interp(Env::empty), "Free Variable: X");

    CHECK((NEW(letExpr)(NEW(VarExpr)("x"), NEW(letExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(6), NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))))->interp(Env::empty)->equals(NEW(numVal)(13)));
}

TEST_CASE("_let equals"){
     PTR(VarExpr) nameVar = NEW(VarExpr)("X");
     PTR(NumExpr) testNum1 = NEW(NumExpr)(5);
     PTR(AddExpr) testAdd1 = NEW(AddExpr)(nameVar, testNum1);
     PTR(letExpr) test1 = NEW(letExpr)(nameVar, testNum1, testAdd1);
     PTR(letExpr) test2 = NEW(letExpr)(nameVar, testAdd1, testNum1);
    CHECK(!(test1->equals(test2)));
    CHECK(!(test1->equals(testNum1)));
}

TEST_CASE("_let print"){
     PTR(VarExpr) nameVar = NEW(VarExpr)("X");
     PTR(NumExpr) testNum1 = NEW(NumExpr)(5);
     PTR(AddExpr) testAdd1 = NEW(AddExpr)(nameVar, testNum1);
     PTR(letExpr) test1 = NEW(letExpr)(nameVar, testNum1, testAdd1);
    CHECK(test1->to_string() == "(_let X=5 _in (X+5))");
}

TEST_CASE("Anna's tests"){

PTR(NumExpr) num1 = NEW(NumExpr)(0);
PTR(NumExpr) num2 = NEW(NumExpr)(-3);
PTR(NumExpr) num3 = NEW(NumExpr)(0);
PTR(NumExpr) num4 = NEW(NumExpr)(3);
//NumExpr num5 = 1231242;
PTR(NumExpr) num6 = NEW(NumExpr)(6);

PTR(VarExpr) var1 = NEW(VarExpr)("hi");
PTR(VarExpr) var2 = NEW(VarExpr)("");

PTR(AddExpr) add1 = NEW(AddExpr)(num1, num1);
PTR(AddExpr) add9 = NEW(AddExpr)(num1, var1);
PTR(AddExpr) add10 = NEW(AddExpr)(var1, var2);

std::string multS1 = "(0*0)";
std::string multS2 = "(-3*3)";
std::string multS12 = "(((-3*3)+0)*(0*0))";
std::string multS13 = "(((-3*3)+(-3*3))*((-3*3)+0))";
std::string multS14 = "((-3*3)*hi)";
std::string multS15 = "((0+0)*0)";

std::string multS1P = "0 * 0";
std::string multS2P = "-3 * 3";
std::string multS12P = "(-3 * 3 + 0) * 0 * 0";
std::string multS13P = "(-3 * 3 + -3 * 3) * (-3 * 3 + 0)";
std::string multS14P = "(-3 * 3) * hi";
std::string multS15P = "(0 + 0) * 0";

PTR(MultExpr)mult1 =NEW(MultExpr)(num1, num1);
PTR(MultExpr)mult2 =NEW(MultExpr)(num2, num4);
PTR(MultExpr)mult3 =NEW(MultExpr)(num3, num3);
PTR(MultExpr)mult4 =NEW(MultExpr)(num4, num2);
PTR(MultExpr)mult5 =NEW(MultExpr)(num1, num3);
PTR(MultExpr)mult6 =NEW(MultExpr)(num3, num1);
//PTR(MultExpr)mult7 =NEW(MultExpr)(num1, num5);
PTR(MultExpr)mult8 =NEW(MultExpr)(num4, num6);
PTR(MultExpr)mult9 =NEW(MultExpr)(num1, var1);
PTR(MultExpr)mult10 =NEW(MultExpr)(var1, var2);
PTR(MultExpr)mult11 =NEW(MultExpr)(var2, num4);

PTR(AddExpr) add12 = NEW(AddExpr)(mult2, num1);
//((-3 * 3) + 0)
PTR(AddExpr) add13 = NEW(AddExpr)(mult2, mult2);
//(-3 * 3) + (-3 * 3)

PTR(MultExpr)mult12 =NEW(MultExpr)(add12, mult1);
//(((-3*3)+0)*(0*0))
//( -3 * 3 + 0 ) * 0 * 0
PTR(MultExpr)mult13 =NEW(MultExpr)(add13, add12);
//(((-3*3)+(-3*3))*((-3*3)+0))
//( -3 * 3 + -3 * 3 ) * ( -3 * 3 + 0 )
PTR(MultExpr)mult14 =NEW(MultExpr)(mult2, var1);
//( -3 * 3 ) * hi
PTR(MultExpr)mult15 =NEW(MultExpr)(add1, num1);
//( 0 + 0 ) * 0

//equals
CHECK(mult1->equals(mult3));
CHECK(!mult1->equals(mult2));
CHECK(!mult1->equals(mult4));
CHECK(mult5->equals(mult6));
CHECK(!mult1->equals(var1));
CHECK(!mult1->equals(num1));
CHECK(!mult1->equals(add1));
//NULL gives a false positive; why?
//CHECK(!mult7.equals(&mult6))

//interp
CHECK(mult8->interp(Env::empty)->equals(NEW(numVal)(18)));
CHECK(mult1->interp(Env::empty)->equals(NEW(numVal)(0)));
CHECK(mult2->interp(Env::empty)->equals(NEW(numVal)(9)) == false);
CHECK(!(mult8->interp(Env::empty)->equals(NEW(numVal)(-18))));

//has_variable
//CHECK(!mult1.has_variable());
//CHECK(mult9.has_variable());
//CHECK(mult10.has_variable());
//CHECK(mult11.has_variable());
//CHECK(mult2.has_variable() == false);
//CHECK_THROWS_WITH(mult10.interp(), "ERROR: This function doesn't support VarExpr" );


//subst
//CHECK( (NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
//               ->subst("x", NEW(VarExpr)("y")) ->equals(NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) );
//CHECK( !(NEW(MultExpr)(NEW(VarExpr)("s"), NEW(NumExpr)(7)))
//        ->subst("x", NEW(VarExpr)("y")) ->equals(NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) );

//print
CHECK(mult1->to_string() == multS1);
CHECK(mult2->to_string() == multS2);
CHECK(mult12->to_string() == multS12);
CHECK(mult13->to_string() == multS13);
CHECK(mult14->to_string() == multS14);
CHECK(mult15->to_string() == multS15);

//pretty_print
CHECK(mult1->to_string_pretty() == multS1P);
CHECK(mult2->to_string_pretty() == multS2P);
CHECK(mult12->to_string_pretty() == multS12P);
CHECK(mult13->to_string_pretty() == multS13P);
CHECK(mult14->to_string_pretty() == multS14P);
CHECK(mult15->to_string_pretty() == multS15P);

}

TEST_CASE("EqExpr"){
     PTR(EqExpr) testOne = NEW(EqExpr)(NEW(NumExpr)(3), NEW(NumExpr)(3));
    CHECK(testOne->to_string() == "(3==3)");
    CHECK(testOne->equals(NEW(EqExpr)(NEW(NumExpr)(3), NEW(NumExpr)(3))) == true);
    CHECK(testOne->equals(NEW(EqExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))) == false);

     PTR(AddExpr) testPrettyPrint = NEW(AddExpr)(testOne, NEW(NumExpr)(55));
    CHECK(testPrettyPrint->pretty_print() == "(3 == 3) + 55");

}

TEST_CASE("BoolExpr"){
     PTR(BoolExpr) testTrue = NEW(BoolExpr)(true);
     PTR(BoolExpr) testFalse = NEW(BoolExpr)(false);

    CHECK(testTrue->equals(testFalse) == false);
    CHECK(testTrue->equals(NEW(NumExpr)(3)) == false);
    CHECK(testTrue->to_string() == "_true");
    CHECK(testFalse->to_string() == "_false");
    CHECK(testFalse->equals(NEW(BoolExpr)(false)) == true);
    CHECK((testTrue->interp(Env::empty))->equals(NEW(BoolVal)(true)));
}

TEST_CASE("_if _then _else"){
     PTR(IfExpr) mattTestIf1 = NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(AddExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(5)), NEW(NumExpr)(88));
     PTR(letExpr) mattTest1 = NEW(letExpr)(NEW(VarExpr)("same"), NEW(EqExpr) (NEW(NumExpr)(1), NEW(NumExpr)(2)), mattTestIf1);
    CHECK(mattTest1->interp(Env::empty)->equals(NEW(numVal)(88)));

     PTR(AddExpr) mattTestAdd1 = NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1));
     PTR(NumExpr) mattTestNum1 = NEW(NumExpr)(2);
     PTR(NumExpr) mattTestNum2 = NEW(NumExpr)(3);
     PTR(IfExpr) mattTestIf2 = NEW(IfExpr)(mattTestAdd1, mattTestNum1, mattTestNum2);
    CHECK_THROWS_WITH(mattTestIf2->interp(Env::empty), "from numVal: is_true function requires type bool");

    CHECK(mattTestIf1->pretty_print() == "_if 1 == 2"
                                         "\n_then _false + 5"
                                         "\n_else 88");
    CHECK(mattTestIf1->to_string() == "( _if (1==2) _then (_false+5) _else 88 )");

//    CHECK(mattTestIf1->has_variable() == false);

    CHECK(mattTestIf1->equals(mattTestIf2) == false);

     PTR(EqExpr) testEqOne = NEW(EqExpr)(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)) , NEW(NumExpr)(3));
     PTR(NumExpr) testEqNum1 = NEW(NumExpr)(88);
     PTR(BoolExpr) testEqBool2 = NEW(BoolExpr)(true);
     PTR(IfExpr) testIf3 = NEW(IfExpr)(testEqOne, testEqNum1, testEqBool2);

    CHECK(testIf3->interp(Env::empty)->equals(NEW(numVal)(88)));
    CHECK(testIf3->equals(testEqBool2) == false);
    CHECK(testIf3->equals(NEW(IfExpr)(NEW(EqExpr)((NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))), NEW(NumExpr)(3)), NEW(NumExpr)(88), NEW(BoolExpr)(true))) == true);

    CHECK(testIf3->pretty_print() == "_if 1 + 2 == 3"
                                     "\n_then 88"
                                     "\n_else _true");

     PTR(AddExpr) testPrettyPrint1 = NEW(AddExpr)(testIf3, NEW(NumExpr)(5));
    CHECK(testPrettyPrint1->interp(Env::empty)->equals(NEW(numVal)(93)));

    CHECK(testPrettyPrint1->pretty_print() == "(_if 1 + 2 == 3"
                                              "\n _then 88"
                                              "\n _else _true) + 5");

     PTR(numVal)  numval1 = NEW(numVal)(1);
     PTR(numVal)  numval2 = NEW(numVal)(2);

     PTR(NumExpr)  num1 = NEW(NumExpr)(1);
     PTR(NumExpr)  num2 = NEW(NumExpr)(2);

     PTR(VarExpr)  varX = NEW(VarExpr)("x");

     PTR(BoolExpr)  T = NEW(BoolExpr)(true);

     PTR(IfExpr)  ifT = NEW(IfExpr)(T, num1, num2);

     PTR(IfExpr)  ifX = NEW(IfExpr)(T, num1, varX);


//    CHECK(ifX->subst("x", num2)->equals(ifT));
}

TEST_CASE("FunExpr"){
    std::string formalArgx = "x";
    std::string formalArgy = "y";

     PTR(AddExpr) testExpr1 = NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1));
     PTR(AddExpr) testExpr2 = NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(1));

     PTR(FunExpr) testFun1 = NEW(FunExpr)(formalArgx, testExpr1);
     PTR(FunExpr) testFun2 = NEW(FunExpr)(formalArgx, testExpr2);

    CHECK(testFun1->interp(Env::empty)->equals(NEW(FunVal)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)), Env::empty)) == true);

    CHECK(testFun1->equals(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));

    CHECK(testFun1->equals(testExpr1) == false);

//    CHECK(testFun1->subst("x", NEW(NumExpr)(1))->equals(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));
//    CHECK(testFun2->subst("y", NEW(NumExpr)(1))->equals(NEW(FunExpr)("x", NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1)))));

    CHECK(testFun1->pretty_print() == "(_fun (x) (x+1))");
}


TEST_CASE("CallExpr"){
    std::string formalArgx = "x";
    std::string formalArgy = "y";

     PTR(AddExpr) testExpr1 = NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1));
     PTR(AddExpr) testExpr2 = NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(1));

     PTR(FunExpr) testFun1 = NEW(FunExpr)(formalArgx, testExpr1);
     PTR(FunExpr) testFun2 = NEW(FunExpr)(formalArgx, testExpr2);

     PTR(CallExpr) testCall1 = NEW(CallExpr)(testFun1, NEW(NumExpr)(1));
     PTR(CallExpr) testCall2 = NEW(CallExpr)(testFun2, NEW(NumExpr)(3));
    CHECK(testCall1->interp(Env::empty)->equals(NEW(numVal)(2)));
    CHECK(testCall1->equals(testFun2) == false);
    CHECK(testCall1->equals(testCall2) == false);

//    CHECK(testCall1->subst("x", NEW(NumExpr)(1))->equals(NEW(CallExpr)(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))), NEW(NumExpr)(1))));

    CHECK(testCall2->pretty_print() == "(_fun (x) (y+1))(3)");



     PTR(AddExpr) XplusOne = NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1));
     PTR(CallExpr) CallOne = NEW(CallExpr)(NEW(FunExpr)("x", XplusOne), NEW(NumExpr)(2));
    std::string result = CallOne->interp(Env::empty)->to_string();
    CHECK(result == "3");
}

//tests from greg
TEST_CASE("FUN Val Testing"){
     PTR(VarExpr) x = NEW(VarExpr)("x");
     PTR(NumExpr) One = NEW(NumExpr)(1);
     PTR(numVal) OneVal = NEW(numVal)(1);
     PTR(numVal) TwoVal = NEW(numVal)(2);

     PTR(AddExpr) XplusOne = NEW(AddExpr)(x, One);

     PTR(FunVal) First = NEW(FunVal)("x", XplusOne, Env::empty);

    std::string FunVal = First->to_string();
    CHECK(FunVal == "(_fun(x) (x+1))");

     PTR(val) result = First->call(OneVal);
    CHECK(result->equals(TwoVal));

}

TEST_CASE("funExprPrint"){
    std::string testString1 = "x";
    PTR(numVal) testNum1 = NEW(numVal)(1);
    PTR(AddExpr) testExpr1 = NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1));
    PTR(FunVal) funVal1 = NEW(FunVal)(testString1, testExpr1, Env::empty);

//    CHECK(funVal1->to_expr()->equals(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x") ,NEW(NumExpr)(1)))));

}