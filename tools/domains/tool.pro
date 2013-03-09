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
    element('Tag',[],Tag),
    Domain-Api
  ) :-
    !,
    member(element('QName',_,[Api]),Tag),
    member(element('tag',_,DTag),Tag),
    member(element('identifier',_,[Domain]),DTag).


%
% Compare facts by the number of APIs associated with a domain
%
domainCompare(Op, Domain1-Apis1, Domain2-Apis2) 
 :-
    length(Apis1,Len1),
    length(Apis2,Len2),
    ( Len1 > Len2, !, Op = '<'
    ; Len1 < Len2, !, Op = '>'
    ; Domain1 @< Domain2, Op = '<'
    ).


%
% Render domain/API association as HTML
%
domainToHtml(
    Domain-Apis,
    element('li',[],[
      element('i',[],[
        Domain,
        ' ',
        '(',
        Atom,
        ')',
        element('ul',[],LIs) ])])
  ) :- 
    length(Apis,Len),
    atom_number(Atom,Len),
    maplist(apiToHtml,Apis,LIs).

apiToHtml(
    Api,
    element('li',[],[
      element('a',[href=URL],[Api]) ])
  ) :-
    atom_concat('http://101companies.org/wiki/Technology:',Api,URL).


%
% Generate HTML for API tags
%
:- 
   load_xml_file('../../metadata/views/Tags for Domains.xml', Xml),
   member(element('view',_,View),Xml),
   member(element('APISelection',_,Apis),View),
   extract(Apis,Facts1),
   keysort(Facts1,Facts2),
   group_pairs_by_key(Facts2,Facts3),
   predsort(domainCompare,Facts3,Facts4),
   maplist(domainToHtml,Facts4,LIs),
   open('output.html', write, Output, []),
   Title = 'Quaatlas: API domains',
   xml_write(Output, [
     element('html',[],[
       element('head',[],[element('title',[],[Title])]),
       element('body',[],[
         element('h1',[],[Title]),
         element('ol',[],LIs)])])], []),
   halt.
