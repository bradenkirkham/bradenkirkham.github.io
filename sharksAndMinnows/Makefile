SRCDIR=../SharksAndMinnowsFinalProject
INCLUDE=-I/opt/homebrew/include/

#compilation flags
CFLAGS=-std=c++11 $(INCLUDE)
#linking flags
LFLAGS=-L/opt/homebrew/lib -lsfml-graphics -lsfml-system -lsfml-window
CC=g++

.PHONY : all
all : SharksAndMinnows

#list of all object files to link together.
OBJS=main.o sharkClass.o userClass.o Textures.o gameFunctions.o

main.o : $(SRCDIR)/main.cpp
	$(CC) $(CFLAGS) -c $<
	
sharkClass.o : $(SRCDIR)/sharkClass.cpp $(SRCDIR)/sharkClass.hpp
	$(CC) $(CFLAGS) -c $<

userClass.o : $(SRCDIR)/userClass.cpp $(SRCDIR)/userClass.hpp
	$(CC) $(CFLAGS) -c $<
	
Textures.o : $(SRCDIR)/Textures.cpp $(SRCDIR)/Textures.hpp
	$(CC) $(CFLAGS) -c $<

gameFunctions.o : $(SRCDIR)/gameFunctions.cpp $(SRCDIR)/gameFunctions.hpp
	$(CC) $(CFLAGS) -c $<

SharksAndMinnows : $(OBJS)
	$(CC) $(LFLAGS) -o SharksAndMinnows $(OBJS)

