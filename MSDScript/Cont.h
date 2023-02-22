//
// Created by Braden Kirkham on 3/28/22.
//
#pragma once

#include <string>
#include "pointer.h"

class Expr;
class Env;
class val;


CLASS (Cont) {
public:
    static PTR(Cont) done;
    virtual void step_continue() = 0;
};

class DoneCont : public Cont {
    void step_continue();
};

class RightThenAddCont : public Cont {
public:
    PTR(Expr) rhs;
    PTR(Env)  env;
    PTR(Cont) rest;
    RightThenAddCont(PTR(Expr) rhs, PTR(Env) env, PTR(Cont) rest);
    void step_continue();
};


class AddCont : public Cont {
public:
    PTR(val) lhs_val;
    PTR(Cont) rest;
    AddCont( PTR(val) lhs_val, PTR(Cont) rest);
    void step_continue();
};

class RightThenMultCont : public Cont {
public:
    PTR(Expr) rhs;
    PTR(Env) env;
    PTR(Cont) rest;
    RightThenMultCont(PTR(Expr) rhs, PTR(Env) env, PTR(Cont) rest);
    void step_continue();
};

class MultCont : public Cont {
public:
    PTR(val) lhs_val;
    PTR(Cont) rest;

    MultCont(PTR(val) lhs_val, PTR(Cont) rest);

    void step_continue();
};

class RightThenEqCont : public Cont {
public:
    PTR(Expr) rhs;
    PTR(Env)  env;
    PTR(Cont) rest;
    RightThenEqCont(PTR(Expr) rhs, PTR(Env)  env, PTR(Cont) rest);
    void step_continue();
};

class EqCont : public Cont {
public:
    PTR(val) lhs_val;
    PTR(Cont) rest;
    EqCont(PTR(val) lhs_val, PTR(Cont) rest);
    void step_continue();
};

class IfBranchCont : public Cont {
public:
    PTR(Expr) _then;
    PTR(Expr) _else;
    PTR(Env) env;
    PTR(Cont) rest;
    IfBranchCont(PTR(Expr) then_part, PTR(Expr) else_part, PTR(Env) env, PTR(Cont) rest);
    void step_continue();
};

class LetBodyCont : public Cont {
public:
    std::string var;
    PTR(Expr) body;
    PTR(Env) env;
    PTR(Cont) rest;
    LetBodyCont(std::string var, PTR(Expr) body, PTR(Env) env, PTR(Cont) rest);
    void step_continue();
};

class ArgThenCallCont : public Cont {
public:
    PTR(Expr) actual_arg;
    PTR(Env)  env;
    PTR(Cont) rest;
    ArgThenCallCont(PTR(Expr) actual_arg, PTR(Env)  env, PTR(Cont) rest);
    void step_continue();
};

class CallCont : public Cont {
public:
    PTR(val)  to_be_called_val;
    PTR(Cont) rest;
    CallCont(PTR(val)  to_be_called_val, PTR(Cont) rest);
    void step_continue();
};