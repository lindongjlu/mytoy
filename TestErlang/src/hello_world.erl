%% @author lindongjlu
%% @doc @todo Add description to hello_world.
%%
%% pwd().
%% cd("E:/workgit/mytoy/TestErlang/src/").
%% c("hello_world").
%% hello_world:printHelloWorld().


-module(hello_world).

%% ====================================================================
%% API functions
%% ====================================================================
-export([printHelloWorld/0]).

printHelloWorld() ->
	io:format("Hello World!~n").

%% ====================================================================
%% Internal functions
%% ====================================================================


