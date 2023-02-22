//
// Created by Braden Kirkham on 2/15/22.
//
#pragma once

#include <string>
#include "pointer.h"
#include "Cont.h"

class Expr;
class Env;
CLASS (val) {
public:
//    virtual  PTR(Expr)  to_expr() = 0;
    virtual bool equals ( PTR(val) e) = 0;
    virtual  PTR(val)  add_to ( PTR(val) e) = 0;
    virtual  PTR(val)  mult_by ( PTR(val) e) = 0;
    virtual std::string to_string() = 0;
    virtual bool is_true() = 0;
    virtual  PTR(val)  call( PTR(val) actual_arg) = 0;
    virtual void call_step(PTR(val) actual_arg_val, PTR(Cont) rest) = 0;
};

class numVal : public val {
public:
    int rep;

    numVal(int rep);
//     PTR(Expr)  to_expr() override;

    bool equals ( PTR(val) e) override;
     PTR(val)  add_to ( PTR(val) e) override;
     PTR(val)  mult_by ( PTR(val) e) override;

    std::string to_string() override;
    bool is_true() override;
    virtual  PTR(val)  call( PTR(val) actual_arg) override;
    void call_step(PTR(val) actual_arg_val, PTR(Cont) rest) override;

};

class BoolVal : public val{
public:
    bool rep;

    BoolVal(bool rep);
//     PTR(Expr)  to_expr() override;

    bool equals ( PTR(val) e) override;
     PTR(val)  add_to ( PTR(val) e) override;
     PTR(val)  mult_by ( PTR(val) e) override;

    std::string to_string() override;
    bool is_true() override;
    virtual  PTR(val)  call( PTR(val) actual_arg) override;
    void call_step(PTR(val) actual_arg_val, PTR(Cont) rest) override;
};

class FunVal : public val{
public:
    std::string formal_arg;
    PTR(Env) env;
    PTR(Expr) body;

    FunVal(std::string formal_arg, PTR(Expr) body, PTR(Env) env);
//     PTR(Expr)  to_expr() override;

    bool equals ( PTR(val) e) override;
     PTR(val)  add_to ( PTR(val) e) override;
     PTR(val)  mult_by ( PTR(val) e) override;

    std::string to_string() override;
    bool is_true() override;
    virtual  PTR(val)  call( PTR(val) actual_arg) override;
    void call_step(PTR(val) actual_arg_val, PTR(Cont) rest) override;
};