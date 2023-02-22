//
// Created by Braden Kirkham on 3/28/22.
//

#include "Cont.h"
#include "Step.h"
#include "Env.h"
#include "pointer.h"
#include "val.h"

PTR(Cont) Cont::done = NEW(DoneCont)();

void DoneCont::step_continue() {
    throw std::runtime_error("Cannot step_continue when done");
}

RightThenAddCont::RightThenAddCont(PTR(Expr) rhs, PTR(Env) env, PTR(Cont) rest) {
    this->rhs = rhs;
    this->env = env;
    this->rest = rest;
}

void RightThenAddCont::step_continue() {
    PTR(val) lhs_val = Step::val;
    Step::mode = Step::interp_mode;
    Step::expr = rhs;
    Step::env = env;
    Step::cont = NEW(AddCont)(lhs_val, rest);
}

AddCont::AddCont( PTR(val) lhs_val, PTR(Cont) rest) {
    this->lhs_val = lhs_val;
    this->rest = rest;
}

void AddCont::step_continue() {
    PTR(val) rhs_val = Step::val;
    Step::mode = Step::continue_mode;
    Step::val = lhs_val->add_to(rhs_val);
    Step::cont = rest;
}

RightThenMultCont::RightThenMultCont(PTR(Expr) rhs, PTR(Env) env, PTR(Cont) rest){
    this->rhs = rhs;
    this->env = env;
    this->rest = rest;
}

void RightThenMultCont::step_continue(){
    PTR(val) lhs_val = Step::val;
    Step::mode = Step::interp_mode;
    Step::expr = rhs;
    Step::env = env;
    Step::cont = NEW(MultCont)(lhs_val, rest);
}

MultCont::MultCont(PTR(val) lhs_val, PTR(Cont) rest){
    this->lhs_val = lhs_val;
    this->rest = rest;
}

void MultCont::step_continue(){
    PTR(val) rhs_val = Step::val;
    Step::mode = Step::continue_mode;
    Step::val = lhs_val->mult_by(rhs_val);
    Step::cont = rest;
}

RightThenEqCont::RightThenEqCont(PTR(Expr) rhs, PTR(Env)  env, PTR(Cont) rest){
    this->rhs = rhs;
    this->env = env;
    this->rest = rest;
}

void RightThenEqCont::step_continue(){
    PTR(val) lhs_val = Step::val;
    Step::mode = Step::interp_mode;
    Step::expr = rhs;
    Step::env = env;
    Step::cont = NEW(EqCont)(lhs_val, rest);
}

EqCont::EqCont(PTR(val) lhs_val, PTR(Cont) rest){
    this->lhs_val = lhs_val;
    this->rest = rest;
}

void EqCont::step_continue(){
    PTR(val) rhs_val = Step::val;
    Step::mode = Step::continue_mode;
    Step::val = NEW(BoolVal)(lhs_val->equals(rhs_val));
    Step::cont = rest;
}

IfBranchCont::IfBranchCont(PTR(Expr) _then, PTR(Expr) _else, PTR(Env) env, PTR(Cont) rest) {
    this->_then = _then;
    this->_else = _else;
    this->env = env;
    this->rest = rest;
}

void IfBranchCont::step_continue(){
    PTR(val) test_val = Step::val;
    Step::mode = Step::interp_mode;
    if (test_val->is_true())
        Step::expr = _then;
    else
        Step::expr = _else;
    Step::env = env;
    Step::cont = rest;
}

LetBodyCont::LetBodyCont(std::string var, PTR(Expr) body, PTR(Env) env, PTR(Cont) rest) {
    this->var = var;
    this->body = body;
    this->env = env;
    this->rest = rest;
}

void LetBodyCont::step_continue(){
    Step::mode = Step::interp_mode;
    Step::expr = body;
    Step::env = NEW(ExtendedEnv)(var, Step::val, env);
    Step::cont = rest;
}

ArgThenCallCont::ArgThenCallCont(PTR(Expr) actual_arg, PTR(Env)  env, PTR(Cont) rest) {
    this->actual_arg = actual_arg;
    this->env = env;
    this->rest = rest;
}

void ArgThenCallCont::step_continue() {
    Step::mode = Step::interp_mode;
    Step::expr = actual_arg;
    Step::env = env;
    Step::cont = NEW(CallCont)(Step::val, rest);
}


CallCont::CallCont(PTR(val)  to_be_called_val, PTR(Cont) rest) {
    this->to_be_called_val = to_be_called_val;
    this->rest = rest;
}

void CallCont::step_continue() {
    to_be_called_val->call_step(Step::val, rest);
}