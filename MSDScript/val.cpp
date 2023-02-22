//
// Created by Braden Kirkham on 2/15/22.
//

#include <sstream>
#include "val.h"
#include "Expr.hpp"
#include "Env.h"
#include "Cont.h"
#include "Step.h"
#include "catch.hpp"

numVal::numVal(int rep){ // numVal holds an int
    this->rep = rep;
}

// PTR(Expr)  numVal::to_expr(){ //converts to Expr
//    return NEW(NumExpr)(this->rep);
//}

bool numVal::equals ( PTR(val) e){ //works same as equals for Expr
     PTR(numVal) addNum = CAST(numVal)(e);
    if (e == NULL){
        return false;
    }
    return rep == addNum->rep;
}

 PTR(val)  numVal::add_to ( PTR(val) e){
     PTR(numVal) addNum = CAST(numVal)(e);
    if (e == NULL){ //checks to make sure both sides are numbers
        throw std::runtime_error("add of non-number"); //error
    }
    return NEW(numVal)(unsigned(rep) + unsigned(addNum->rep)); // return a NEW(numVal) of the sum of both.

}

 PTR(val)  numVal::mult_by ( PTR(val) e){ // same as add but with multiplication
     PTR(numVal) addNum = CAST(numVal)(e);
    if (e == NULL){
        throw std::runtime_error("mult of non-number");
    }
    return NEW(numVal)(unsigned(rep) * unsigned(addNum->rep));
}


std::string numVal::to_string (){ // converts to string helper function
    return std::to_string(this->rep);
}

bool numVal::is_true() {
    throw std::runtime_error("from numVal: is_true function requires type bool");
}

 PTR(val)  numVal::call( PTR(val) actual_arg) {
    throw std::runtime_error("call of non-FunVal");
}

void numVal::call_step(PTR(val) actual_arg_val, PTR(Cont) rest) {
    throw std::runtime_error ("call_step of non-FunVal");
}

//BoolVal functions
BoolVal::BoolVal(bool rep){ //constructor for BoolVal holds boolean.
    this->rep = rep;
}

// PTR(Expr)  BoolVal::to_expr() { // conversion to expr.
//    return NEW(BoolExpr)(rep);
//}

bool BoolVal::equals ( PTR(val) e) {
     PTR(BoolVal) castBool = CAST(BoolVal)(e);
    if (e == NULL){
        return false;
    }
    return rep == castBool->rep;
}

 PTR(val)  BoolVal::add_to ( PTR(val) e){
    throw std::runtime_error("add of non-number");
}

 PTR(val)  BoolVal::mult_by ( PTR(val) e) {
    throw std::runtime_error("add of non-number");
}

std::string BoolVal::to_string() { // to string helper function
    if (rep == true) {
        return "_true";
    } else {
        return "_false";
    }
}

bool BoolVal::is_true() { // simply return the member var boolean value.
    return rep;
}

 PTR(val)  BoolVal::call( PTR(val) actual_arg) {
    throw std::runtime_error ("call of non-FunVal");
}

void BoolVal::call_step(PTR(val) actual_arg_val, PTR(Cont) rest) {
    throw std::runtime_error ("call_step of non-FunVal");
}

//FunVal
FunVal::FunVal(std::string formal_arg, PTR(Expr) body, PTR(Env) env){ // constructor of FunVal, takes a string and an expr
    this->formal_arg = formal_arg;
    this->env = env;
    this->body = body;
}

// PTR(Expr)  FunVal::to_expr(){
//    return NEW(FunExpr)(formal_arg, body);
//}

bool FunVal::equals ( PTR(val) e){
     PTR(FunVal) castFun = CAST(FunVal)(e);
    if (e == NULL){
        return false;
    }
    return (formal_arg == castFun->formal_arg && body->equals(castFun->body));
}

 PTR(val)  FunVal::add_to ( PTR(val) e){
    throw std::runtime_error("add of non-number");
}

 PTR(val)  FunVal::mult_by ( PTR(val) e){
    throw std::runtime_error("mult of non-number");
}

std::string FunVal::to_string(){
    std::string newString = "(_fun(";

    newString += formal_arg;
    newString += ") ";
    newString += body->to_string();
    newString += ")";

    return newString;
}

bool FunVal::is_true(){
    throw std::runtime_error("from FunVal: is_true function requires type bool");
}

 PTR(val)  FunVal::call( PTR(val) actual_arg) { // solves a function
     return body->interp(NEW(ExtendedEnv)(formal_arg, actual_arg, this->env)); // call subst on body replacing formal_arg if found with actual_arg expr.
}

void FunVal::call_step(PTR(val) actual_arg_val, PTR(Cont) rest) {
    Step::mode = Step::interp_mode;
    Step::expr = body;
    Step::env = NEW(ExtendedEnv)(formal_arg, actual_arg_val, env);
    Step::cont = rest;
}


TEST_CASE("numVal"){
     PTR(numVal) testVal1 = NEW(numVal)(3);

    CHECK_THROWS_WITH(testVal1->add_to(nullptr), "add of non-number");
    CHECK(testVal1->equals(nullptr) == false);
    CHECK_THROWS_WITH(testVal1->mult_by(nullptr), "mult of non-number");
    CHECK_THROWS_WITH(testVal1->call(NEW(numVal)(1)), "call of non-FunVal");

    CHECK(testVal1->to_string() == "3");
}

TEST_CASE("BoolVal"){
     PTR(BoolVal) testTrue = NEW(BoolVal)(true);
     PTR(BoolVal) testFalse = NEW(BoolVal)(false);

    CHECK_THROWS_WITH(testTrue->mult_by(NEW(numVal)(55)), "add of non-number");
    CHECK_THROWS_WITH(testTrue->add_to(NEW(numVal)(55)), "add of non-number");

    CHECK(testFalse->equals(nullptr) == false);
    CHECK(testFalse->to_string() == "_false");
    CHECK(testTrue->to_string() == "_true");

    CHECK_THROWS_WITH(testFalse->call(NEW(numVal)(1)), "call of non-FunVal");


}

TEST_CASE("FunVal"){
    std::string testString1 = "x";
     PTR(numVal) testNum1 = NEW(numVal)(1);
     PTR(AddExpr) testExpr1 = NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1));
     PTR(FunVal) funVal1 = NEW(FunVal)(testString1, testExpr1, Env::empty);

    CHECK_THROWS_WITH(funVal1->is_true(),"from FunVal: is_true function requires type bool");
    CHECK(funVal1->equals(nullptr) == false);
//    CHECK(funVal1->to_expr()->equals(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x") ,NEW(NumExpr)(1)))));

    CHECK_THROWS_WITH(funVal1->add_to(testNum1), "add of non-number");
    CHECK_THROWS_WITH(funVal1->mult_by(testNum1), "mult of non-number");
}