//
// Created by Braden Kirkham on 3/21/22.
//

#include "Env.h"
#include <string>

PTR(Env) Env::empty = NEW(EmptyEnv)();

EmptyEnv::EmptyEnv(){}

PTR(val) EmptyEnv::lookup(std::string findName){
    throw std::runtime_error("Free Variable: " + findName);
}

ExtendedEnv::ExtendedEnv(std::string name, PTR(val) Val, PTR(Env) rest){
    this->name = name;
    this->Val = Val;
    this->rest = rest;
}
PTR(val) ExtendedEnv::lookup(std::string findName){
    if (findName == name){
        return Val;
    }
    else{
        return rest->lookup(findName);
    }
}