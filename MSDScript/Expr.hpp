
#pragma once
#include <string>
#include "pointer.h"

class val;
class Env;

typedef enum { // precedence to help with parenthesis
    prec_none,      // = 0
    prec_equals,    // = 1
    prec_add,       // = 2
    prec_mult       // = 3
} precedence_t;

CLASS (Expr){
public:
    virtual bool equals ( PTR(Expr) e) = 0;
    virtual  PTR(val)  interp (PTR(Env) env) = 0;
    virtual void step_interp() = 0;
//    virtual bool has_variable() = 0;
//    virtual  PTR(Expr)  subst(std::string var, PTR(Expr) e) = 0;
    virtual void print(std::ostream& output) = 0;
    std::string to_string ();
    virtual std::string pretty_print();
    virtual void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) = 0;
    std::string to_string_pretty();

};

class NumExpr : public Expr {
public:
    int rep;

    NumExpr (int rep);

    bool equals( PTR(Expr) e) override;

    PTR(val)  interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class BoolExpr : public Expr {
public:
    bool rep;

    BoolExpr (bool rep);

    bool equals( PTR(Expr) e) override;

    PTR(val) interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class EqExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;

    EqExpr( PTR(Expr) lhs, PTR(Expr) rhs);

    bool equals( PTR(Expr) e) override;

    PTR(val) interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class AddExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;

    AddExpr ( PTR(Expr) lhs, PTR(Expr) rhs);

    bool equals( PTR(Expr) e) override;

    PTR(val)  interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class MultExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;

    MultExpr ( PTR(Expr) lhs, PTR(Expr) rhs);

    bool equals( PTR(Expr) e) override;

    PTR(val) interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class VarExpr : public Expr {
public:
    std::string rep;

    VarExpr (std::string val);

    bool equals( PTR(Expr) e) override;

    PTR(val) interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class letExpr : public Expr {
public:
     PTR(VarExpr) lhs;
    PTR(Expr) rhs;
    PTR(Expr) body;

    letExpr ( PTR(VarExpr) lhs, PTR(Expr) rhs, PTR(Expr) body);

    bool equals( PTR(Expr) e) override;

    PTR(val)  interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class IfExpr : public Expr {
public:
    PTR(Expr) _if;
    PTR(Expr) _then;
    PTR(Expr) _else;

    IfExpr( PTR(Expr) _if, PTR(Expr) _then, PTR(Expr) _else);

    bool equals( PTR(Expr) e) override;

    PTR(val)  interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class FunExpr : public Expr{
public:
    std::string formal_arg;
    PTR(Expr) body;

    FunExpr(std::string formal_arg, PTR(Expr) body);

    bool equals( PTR(Expr) e) override;

    PTR(val) interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};

class CallExpr : public Expr{
public:
    PTR(Expr) to_be_called;
    PTR(Expr) actual_arg;

    CallExpr( PTR(Expr) to_be_called, PTR(Expr) actual_arg);

    bool equals( PTR(Expr) e) override;

    PTR(val)  interp(PTR(Env) env) override;

    void step_interp() override;

//    bool has_variable() override;

//     PTR(Expr)  subst(std::string var, PTR(Expr) e) override;

    void print(std::ostream& output) override;

    void pretty_print_at(std::ostream& output, precedence_t prec, bool needsparens, std::streampos& streampos) override;

};