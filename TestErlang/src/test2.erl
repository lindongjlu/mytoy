%% @author francislin
%% @doc @todo Add description to test2.


-module(test2).

%% ====================================================================
%% API functions
%% ====================================================================
-export([testEcho/0, testEcho_loop/0]).

%% ====================================================================
%% Echo Process
%% ====================================================================

testEcho() ->
	Pid2 = spawn(test2, testEcho_loop, []),
	Pid2 ! {self(), hello},
	receive 
		{Pid2, Msg} ->
			io:format("P1 ~w~n",[Msg])
	end,
	Pid2 ! stop.

testEcho_loop() ->
	receive
		{From, Msg} -> 
			From ! {self(), Msg},
			testEcho_loop();
		stop ->
			true
	end.


%% ====================================================================
%% Internal functions
%% ====================================================================


