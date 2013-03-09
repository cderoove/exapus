:- use_module(library(sgml)).

%
% Extract facts from API selection
%
extract([],[]).
extract([H|T],L) :- 
  atom(H),
  !,
  extract(T,L).
extract([H1|T1],[H2|T2]) :- 
  !,
  extract(H1,H2),
  extract(T1,T2).
extract(
    element(PfxPkg,[],Api),
    (Id,Display,PfxPkg,QName)
  ) :-
    member(PfxPkg, ['Prefix', 'Package']),
    member(element('QName',_,[QName]),Api),
    member(element('tag',_,Tag),Api),
    member(element('identifier',_,[Id]),Tag),
    ( member(element('display',_,[Display]),Tag) ->
        true
      ; Display = ''
    ).


%
% Compare facts by the URI name
%
apiCompare(Op, (Id1,_,_,_), (Id2,_,_,_)) 
 :-
      Id1 == Id2, !, Op = '='
    ; Id1 @< Id2, !, Op = '<'
    ; Op = '>'.


%
% Render API selection facts as HTML
%
tohtml(
    (Id,Display,PfxPkg,QName),
    element('tr',[],[
      element('td',[],[Link]), 
      element('td',[],[Display]), 
      element('td',[],[PfxPkg]),
      element('td',[],[QName]) ])
  ) :- 
    atom_concat('http://101companies.org/wiki/Technology:',Id,URL),
    Link = element('a',[href=URL],[Id]).


%
% Generate HTML for API tags
%
:- 
   load_xml_file('../../metadata/views/Tags for APIs.xml', Xml),
   member(element('view',_,View),Xml),
   member(element('APISelection',_,Apis),View),
   extract(Apis,Facts1),
   predsort(apiCompare,Facts1,Facts2),
   Header = element('tr',[],[
     element('th',[],['URI name']),
     element('th',[],['Display name']),
     element('th',[],['Scope']),
     element('th',[],['Package'])]),
   maplist(tohtml,Facts2,Rows),
   open('output.html', write, Output, []),
   Title = 'Quaatlas: APIs',
   xml_write(Output, [
     element('html',[],[
       element('head',[],[element('title',[],[Title])]),
       element('body',[],[
         element('h1',[],[Title]),
         element('table',[border=1],[Header|Rows])])])], []),
   halt.
