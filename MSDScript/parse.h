//
// Created by Braden Kirkham on 2/4/22.
//
#pragma once

#include <istream>
#include "pointer.h"

class Expr;

 PTR(Expr) parse(std::istream &inputStream);

static PTR(Expr) parse_expr(std::istream &inputStream);

static PTR(Expr) parse_comparg(std::istream &inputStream);

static PTR(Expr) parse_addend(std::istream &inputStream);

static PTR(Expr) parse_mult(std::istream &inputStream);

static PTR(Expr) parse_inner(std::istream &inputStream);

static PTR(Expr) parse_num(std::istream &inputStream);

static PTR(Expr) parse_let(std::istream &inputStream);

static PTR(Expr) parse_If(std::istream &inputStream);

static PTR(Expr) parse_fun(std::istream &inputstream);

static PTR(Expr) parse_var(std::istream &inputStream);

static void *parse_keyWord(std::istream &inputStream, std::string keyWord, bool isif);

static void consume(std::istream &in, int expected);

void skip_whitespace(std::istream &inputStream);

PTR(Expr) parse_str(std::string s);

//static  PTR(Expr)  parseUnderscore(std::istream &inputStream);