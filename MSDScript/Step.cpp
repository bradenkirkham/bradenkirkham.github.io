//
// Created by Braden Kirkham on 3/28/22.
//

#include "Step.h"
#include "Env.h"
#include "Cont.h"
#include "Expr.hpp"
#include <iostream>

Step::mode_t Step::mode = interp_mode;

PTR(Expr) Step::expr = nullptr;
PTR(Env) Step::env = NEW(EmptyEnv)();
PTR(val) Step::val = nullptr;
PTR(Cont) Step::cont = nullptr;

PTR(val) Step::interp_by_steps(PTR(Expr) e) {
    Step::mode = Step::interp_mode;
    Step::expr = e;
    Step::env = Env::empty;
    Step::val = nullptr;
    Step::cont = Cont::done;
    while (1) {
        if (Step::mode == Step::interp_mode) {
//            std::cout << "Interpreting: \n" << Step::expr->to_string() << std::endl;
            Step::expr->step_interp();
        }
        else {
            if (Step::cont == Cont::done) {
//                std::cout << "Continuing with:\n" << Step::val << std::endl;
                return Step::val;
            }
            else {
                Step::cont->step_continue();
            }
        }
    }
}