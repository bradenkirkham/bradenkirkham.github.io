//
// Created by Braden Kirkham on 3/28/22.
//

#pragma once
#include "pointer.h"

class Expr;
class Env;
class val;
class Cont;

CLASS (Step) {
public:
    typedef enum {
        interp_mode,
        continue_mode
    } mode_t;

    static PTR(val) interp_by_steps(PTR(Expr) e);

    static mode_t mode;    /* chooses mode */
    static PTR(Expr) expr; /* for interp_mode */
    static PTR(Env)  env;  /* for interp_mode */
    static PTR(val)  val;  /* for continue_mode */
    static PTR(Cont) cont; /* all modes */

};

