:- use_module(library(sgml)).

%
% Extract facts from API selection
%
extract([],[]).
extract([H|T],L) :- 
  atom(H),
  !,
  extract(T,L).
extract([H|T],L) :- 
  H = element('Universal',_,_),
  !,
  extract(T,L).
extract([H1|T1],[H2|T2]) :-
  extract(H1,H2),
  extract(T1,T2).
extract(
    element(Scope,[],Tag1),
    Super-(Sub-(Scope,QName))
  ) :-
    !,
    member(Scope,['Prefix','Type','Method']),
    member(element('QName',_,[QName]),Tag1),
    member(element('tag',_,Tag2),Tag1),
    member(element('identifier',_,[Sub]),Tag2),
    member(element('associatedName',_,[Super]),Tag2).


%
% Group all entries by the sub-API
%
groupBySub(Super-L1,Super-L3)
 :-
    keysort(L1,L2),
    group_pairs_by_key(L2,L3).


%
% Compare facts by scope and qname
%
selCompare(Op, (Scope1,QName1), (Scope2,QName2)) 
 :-
    ( Scope1 = Scope2, !,
      ( QName1 @< QName2, !, Op = '<'
      ; QName1 @> QName2, !, Op = '>'
      ; Op = '='
      )
    ; Scope1 = 'Prefix', !, Op = '<'
    ; Scope2 = 'Prefix', !, Op = '>'
    ; Scope1 = 'Package', !, Op = '<'
    ; Scope2 = 'Package', !, Op = '>'
    ; Scope1 = 'Type', !, Op = '<'
    ; Scope2 = 'Type', !, Op = '>'
    ; Scope1 = 'Method', !, Op = '<'
    ; Scope2 = 'Method', !, Op = '>' 
    ).


%
% Render breakdown of API as HTML
%
superToHtml(Super-L) :-
  atom_concat(Super,'.html',Target),
  open(Target, write, Output, []),
  Title = 'Quaatlas: API breakdown',
  maplist(subToHtml,L,LIs),
  xml_write(Output, [
    element('html',[],[
      element('head',[],[element('title',[],[Title])]),
      element('body',[],[
        element('h1',[],[Title, ' for ', element('i',[],[Super])]),
        element('ul',[],LIs) ])])], []).

subToHtml(
    Sub-L1,
    element('li',[],[
      element('b',[],[Sub]),
      element('ul',[],LIs) ])
  ) :-
    predsort(selCompare,L1,L2),
    maplist(selToHtml,L2,LIs).

selToHtml(
    (Scope,QName),
    element('li',[],[Scope, ' ', element('i',[],[QName])])
  ).


%
% Generate HTML for API tags
%
:- 
   load_xml_file('../../metadata/views/Tags for Sub-APIs.xml', Xml),
   member(element('view',_,View),Xml),
   member(element('APISelection',_,Apis),View),
   extract(Apis,Facts1),
   keysort(Facts1,Facts2),
   group_pairs_by_key(Facts2,Facts3),
   maplist(groupBySub,Facts3,Facts4),
   maplist(superToHtml,Facts4),
   halt.
