//
// Created by Braden Kirkham on 3/21/22.
//

#pragma once
#include <string>
#include "pointer.h"

class val;
CLASS (Env) {
public:
    virtual PTR(val) lookup(std::string findName) = 0;
    static PTR(Env) empty;
};

class EmptyEnv : public Env{
public:
    EmptyEnv();
    PTR(val) lookup(std::string findNAme);
};

class ExtendedEnv : public Env{
public:
    std::string name;
    PTR(val) Val;
    PTR(Env) rest;

    ExtendedEnv(std::string name, PTR(val) Val, PTR(Env) rest);
    PTR(val) lookup(std::string findNAme);
};

