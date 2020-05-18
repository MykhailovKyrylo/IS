:- initialization(main).

filterGraph([[V0, C, V1]], State, Edges, Result):-
    V0 = State ->
        append(Edges, [[V0, C, V1]], Result);
    append(Edges, [], Result),   
    !.
filterGraph([[V0, C, V1]|Tail], State, Edges, Result):-
    V0 = State ->
        append(Edges, [[V0, C, V1]], NextEdges),
        filterGraph(Tail, State, NextEdges, Result);
    filterGraph(Tail, State, Edges, Result).

goNextState(Word, State, [], FinalStates, K, Graph):- !.
goNextState(Word, State, [[V0, C, V1]], FinalStates, K, Graph):-
    atom_concat(Word, C, NextWord),
    atom_length(NextWord, Length),
    Length =< K ->
        problem12(NextWord, V1, Graph, FinalStates, K);
    !.
goNextState(Word, State, [[V0, C, V1]|Tail], FinalStates, K, Graph):-
    atom_concat(Word, C, NextWord),
    atom_length(NextWord, Length),
    Length =< K ->
        problem12(NextWord, V1, Graph, FinalStates, K),
        goNextState(Word, State, Tail, FinalStates, K, Graph);
    atom_concat(Word, C, NextWord),
    goNextState(Word, State, Tail, FinalStates, K, Graph).
    
problem12(Word, State, Graph, FinalStates, K):-
    atom_length(Word, Length),
    (Length = K, member(State, FinalStates)) ->
        print(Word),
        nl;
    filterGraph(Graph, State, [], NextEdges),
    goNextState(Word, State, NextEdges, FinalStates, K, Graph).
    
main :- problem12('', 0, [[0, 'a', 1], [0, 'b', 7], [0, 'c', 0], [1, 'a', 1], [1, 'b', 2], [1, 'c', 4], [2, 'a', 2], 
[2, 'c', 3], [3, 'b', 3], [3, 'c', 4], [4, 'b', 2], [5, 'c', 4], [6, 'a', 4], [6, 'b', 6], [6, 'c', 6]], [2, 3, 4], 2). 