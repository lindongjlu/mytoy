%% @author lindongjlu
%% @doc @todo Add description to my_manual.


-module(test1).

%% ====================================================================
%% API functions
%% ====================================================================

-export([printNumbers/0]).
-export([printAtoms/0]).
-export([printTuples/0]).
-export([printLists/0]).
-export([testPatternMatching/0]).
-export([testPatternMatchingCont/0]).
-export([testBuiltInFunctions/0]).
-export([testGuardedFunctionClauses/0]).
-export([testTraversingLists/0]).
-export([testSpecialFunctions/0]).
-export([testSpecialForms/0]).

-export([sum/1]).

%% ====================================================================
%% Numbers
%% ====================================================================

printNumbers() ->
	io:format("Integer: [10]=~b~n", [10]),
	io:format("B#Val is used to store numbers in base <B>: [2#1000]=~b~n", [2#1000]),
	io:format("$Char is used for ascii values (example $A instead of 65): [$A]=~b~n", [$A]),
	io:format("Float: [17.368]=~f~n", [17.368]),
	io:format("Float: [-56.654]=~f~n", [-56.654]),
	io:format("Float: [12.34E-3]=~f~n", [12.34E-3]).

%% ====================================================================
%% Atoms
%% ====================================================================

printAtoms() ->
	io:format("Atoms: [abcef] -> ~s~n", [abcdef]),
	io:format("Atoms: [start_with_a_lower_case_letter] -> ~s~n", [start_with_a_lower_case_letter]),
	io:format("Atoms: ['Blanks can be quoted'] -> ~s~n", ['Blanks can be quoted']),
	io:format("Atoms: ['Anything inside quotes \n\012'] -> ~s~n", ['Anything inside quotes \n\012']).

%% ====================================================================
%% Tuples
%% ====================================================================

printTuples() ->
	io:format("Tuples: [{123, bcd}] -> ~s~n", [io_lib:write({123, bcd})]),
	io:format("Tuples: [{123, def, abc}] -> ~s~n", [io_lib:write({123, def, abc})]),
	io:format("Tuples: [{person, 'Joe', 'Armstrong'}] -> ~s~n", [io_lib:write({person, 'Joe', 'Armstrong'})]),
	io:format("Tuples: [{abc, {def, 123}, jkl}] -> ~s~n", [io_lib:write({abc, {def, 123}, jkl})]),
	io:format("Tuples: [{}] -> ~s~n", [io_lib:write({})]).

%% ====================================================================
%% Lists
%% ====================================================================

printLists() ->
	io:format("List: [123, xyz] -> ~s~n", [io_lib:write([123, xyz])]),
	io:format("List: [123, def, abc] -> ~s~n", [io_lib:write([123, def, abc])]),
	io:format("List: [{person, 'Joe', 'Armstrong'}, {person, 'Robert', 'Virding'}, {person, 'Mike', 'Williams'}] -> ~s~n", 
			  [io_lib:write([{person, 'Joe', 'Armstrong'}, {person, 'Robert', 'Virding'}, {person, 'Mike', 'Williams'}])]),
	io:format("List: \"abcdefghi\" -> ~s~n", [io_lib:write("abcdefghi")]),
	io:format("List: \"\" -> ~s~n", [io_lib:write("")]).

%% ====================================================================
%% Pattern Matching
%% ====================================================================

testPatternMatching() ->
	A = 10,
	io:format("A = ~s~n", [io_lib:write(A)]),
	{B, C, D} = {10, foo, bar},
	io:format("{B, C, D} = ~s~n", [io_lib:write({B, C, D})]),
	{E, E, F} = {abc, abc, foo},
	io:format("{E, E, F} = ~s~n", [io_lib:write({E, E, F})]),
	%% Error:
	%% {G, G, H} = {abc, def, 123},
	%% io:format("{G, G, H} = ~s~n", [io_lib:write({G, G, H})]),
	[I,J,K] = [1,2,3],
	io:format("[I,J,K] = ~s~n", [io_lib:write([I,J,K])]).
	%% Error:
	%% [L,M,N,O] = [1,2,3],
	%% io:format("[L,M,N,O] = ~s~n", [io_lib:write([L,M,N,O])]).

testPatternMatchingCont() ->
	[A,B|C] = [1,2,3,4,5,6,7],
	io:format("[A,B|C] = [1,2,3,4,5,6,7], A=~s, B=~s, C=~s~n", [io_lib:write(A), io_lib:write(B), io_lib:write(C)]),
	[H|T] = [1,2,3,4],
	io:format("[H|T] = [1,2,3,4], H=~s, T=~s~n", [io_lib:write(H), io_lib:write(T)]), 
	[D|E] = [abc],
	io:format("[D|E] = [abc], D=~s, E=~s~n", [io_lib:write(D), io_lib:write(E)]),
	{X,_, [Y|_],{Y}} = {abc,23,[22,x],{22}},
	io:format("{X,_, [Y|_],{Y}} = {abc,23,[22,x],{22}}, X=~s, Y=~s~n", [io_lib:write(X), io_lib:write(Y)]).

%% ====================================================================
%% Built In Functions
%% ====================================================================

testBuiltInFunctions() ->
	io:format("date() -> ~s~n", [io_lib:write(date())]),
	io:format("time() -> ~s~n", [io_lib:write(time())]),
	io:format("length([1,2,3,4,5]) -> ~s~n", [io_lib:write(length([1,2,3,4,5]))]),
	io:format("atom_to_list(an_atom) -> ~s~n", [io_lib:write(atom_to_list(an_atom))]),
	io:format("list_to_tuple([1,2,3,4]) -> ~s~n", [io_lib:write(list_to_tuple([1,2,3,4]))]),
	io:format("integer_to_list(2234) -> ~s~n", [io_lib:write(integer_to_list(2234))]),
	io:format("tuple_to_list({}) -> ~s~n", [io_lib:write(tuple_to_list({}))]).

%% ====================================================================
%% Guarded Function Clauses
%% ====================================================================

testGuardedFunctionClauses() ->
	io:format("factorial(5) -> ~s~n", [io_lib:write(factorial(5))]),
	io:format("factorial(0) -> ~s~n", [io_lib:write(factorial(0))]),
	testExampleOfGuards(123),
	testExampleOfGuards(234.567),
	testExampleOfGuards(abc),
	testExampleOfGuards({abc}),
	testExampleOfGuards([234, abc]),
	testExampleOfGuards([234, abc, 78.12]),
	testExampleOfGuards({234, abc}),
	testExampleOfGuards2(3, 1, 1),
	testExampleOfGuards3(1, 1),
	testExampleOfGuards3(1, 1.0).


factorial(N) when N > 0 ->
	N * factorial(N - 1);
factorial(0) -> 1.

testExampleOfGuards(X) when length(X) == 3 ->
	io:format("~s is length(X) == 3~n", [io_lib:write(X)]);

testExampleOfGuards(X) when size(X) == 2 ->
	io:format("~s is size(X) == 2~n", [io_lib:write(X)]);

%% testExampleOfGuards(X) when is_number(X)->
%% 	io:format("~s is number~n", [io_lib:write(X)]);

testExampleOfGuards(X) when is_integer(X) ->
	io:format("~s is integer~n", [io_lib:write(X)]);

testExampleOfGuards(X) when is_float(X) ->
	io:format("~s is float~n", [io_lib:write(X)]);

testExampleOfGuards(X) when is_atom(X) ->
	io:format("~s is atom~n", [io_lib:write(X)]);

testExampleOfGuards(X) when is_tuple(X) ->
	io:format("~s is tuple~n", [io_lib:write(X)]);

testExampleOfGuards(X) when is_list(X) ->
	io:format("~s is list~n", [io_lib:write(X)]).

testExampleOfGuards2(X, Y, Z) when X > Y + Z ->
	io:format("~s > ~s + ~s~n", [io_lib:write(X), io_lib:write(Y), io_lib:write(Z)]).

testExampleOfGuards3(X, Y) when X =:= Y->
	io:format("~s =:= ~s~n", [io_lib:write(X), io_lib:write(Y)]);

testExampleOfGuards3(X, Y) when X == Y->
	io:format("~s == ~s~n", [io_lib:write(X), io_lib:write(Y)]).

%% ====================================================================
%% Traversing Lists
%% ====================================================================

testTraversingLists() ->
	io:format("average([1,2,3,4,5]) = ~f~n", [average([1,2,3,4,5])]),
	io:format("average2([1,2,3,4,5]) = ~f~n", [average2([1,2,3,4,5])]),
	io:format("double([1,2,3,4,5]) = ~s~n", [io_lib:write(double([1,2,3,4,5]))]),
	io:format("member(5, [1,2,3,4,5]) = ~s~n", [member(5, [1,2,3,4,5])]).

average(X) -> sum(X) / len(X).
	
sum([H|T]) -> H + sum(T);
sum([]) -> 0.

len([_|T]) -> 1 + len(T);
len([]) -> 0.

average2(X) -> average2(X, 0, 0).

average2([H|T], Length, Sum) ->
	average2(T, Length + 1, Sum + H);
average2([], Length, Sum) ->
	Sum / Length.

double([H|T]) -> [2*H|double(T)];
double([]) -> [].

member(H, [H|_]) -> true;
member(H, [_|T]) -> member(H, T);
member(_, []) -> false.

%% ====================================================================
%% Special Functions
%% ====================================================================

testSpecialFunctions() ->
	io:format("apply(lists, reverse,[[4,1,7,3,9,10]]) = ~s~n", [io_lib:write(apply(lists, reverse,[[4,1,7,3,9,10]]))]),
	io:format("apply(my_manual,sum,[[4,1,7,3,9,10]]) = ~b~n", [apply(my_manual,sum,[[4,1,7,3,9,10]])]).

%% ====================================================================
%% Special Forms
%% ====================================================================

testSpecialForms() ->
	testSpecialFormsCase(1, [1,2,3]),
	testSpecialFormsIf(123),
	testSpecialFormsIf({abc}),
	testSpecialFormsIf([123,abc]).

testSpecialFormsCase(A, X) ->
	case lists:member(A, X) of
		true ->
			io:format("case lists:member(~s, ~s) = true~n", [io_lib:write(A), io_lib:write(X)]);
		false -> 
			io:format("case lists:member(~s, ~s) = false~n", [io_lib:write(A), io_lib:write(X)])
	end.

testSpecialFormsIf(X) ->
	if
		is_integer(X) -> io:format("if is_integer(~s) = true~n", [io_lib:write(X)]);
		is_tuple(X) -> io:format("if is_tuple(~s) = true~n", [io_lib:write(X)])
	end.