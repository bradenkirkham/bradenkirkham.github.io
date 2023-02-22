INCS = cmdline.hpp Expr.hpp parse.h val.h pointer.h Env.h Cont.h Step.h

TESTINCS = MSDTest/testGeneratorFunctions.h MSDTest/exec.h

OBJS = main.o cmdline.o Expr.o parse.o val.o Env.o Cont.o Step.o

OBJSTest = test_msdscript.o testGeneratorFunctions.o exec.o

LIBOBJS = cmdline.o Expr.o parse.o val.o Env.o Cont.o Step.o

CLEAN_OBJS = msdscript test_msdscript

CXX = c++

CXXFLAGS = --std=c++14 -O2 #-fsanitize=undefined -fno-sanitize-recover=undefined

msdscript: $(OBJS)
		$(CXX) $(CXXFLAGS) -o msdscript $(OBJS)

test_msdscript: $(OBJSTest)
		$(CXX) $(CXXFLAGS) -o test_msdscript $(OBJSTest)

libmsdscript.a: $(LIBOBJS)
	ar -ruv libmsdscript.a $(LIBOBJS)

main.o: main.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c main.cpp

cmdline.o: cmdline.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c cmdline.cpp

Expr.o: Expr.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c Expr.cpp

parse.o: parse.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c parse.cpp

val.o: val.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c val.cpp

Env.o: Env.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c Env.cpp

Cont.o: Cont.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c Cont.cpp

Step.o: Step.cpp $(INCS)
		$(CXX) $(CXXFLAGS) -c Step.cpp

test_msdscript.o: MSDTest/test_msdscript.cpp $(TESTINCS)
		$(CXX) $(CXXFLAGS) -c MSDTest/test_msdscript.cpp

testGeneratorFunctions.o: MSDTest/testGeneratorFunctions.cpp $(TESTINCS)
		$(CXX) $(CXXFLAGS) -c MSDTest/testGeneratorFunctions.cpp

exec.o: MSDTest/exec.cpp $(TESTINCS)
		$(CXX) $(CXXFLAGS) -c MSDTest/exec.cpp

.PHONY: test
test: msdscript
	./msdscript --test

.PHONY: clean
clean:
	-rm *.o *.a $(CLEAN_OBJS)