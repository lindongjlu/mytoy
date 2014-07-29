%% @author francislin
%% @doc @todo Add description to hello_world.


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


