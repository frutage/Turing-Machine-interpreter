; This tuing program checks whether the input string can be splitted into two equal substrings.

; the finite set of states
#Q = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,accept,accept2,accept3,accept4,halt_accept,reject,reject1,reject2,reject3,reject4,reject5,halt_reject}

; the finite set of input symbols
#S = {a,b}

; the set of tape symbols
#T = {a,b,c,_,T,r,u,e,F,l,s}

; the blank symbol
#B = _

; the start symbol
#q0 = 1

; the set of final states
#F = {halt_accept}

;state meaning:
;1: start state
;2: meet 'a'
;3: meet 'b'
;4: exchange 'c' with scanned char
;5: move right until it meets 'c' or blank
;6: find the right 'c'
;7: meet 'a'
;8: meet 'b'
;9: exchange 'c' with scanned char
;10: move left until it meets 'c'
;11: the input string has been split into two parts
;12: the right part starts with 'a' and move left
;13: the right part starts with 'b' and move right
;14: move to the leftmost char
;15: move to the rightmost char
;16: left start equals the right start, continue
;17: two substring are equal
;accept: eliminate all non-blank symbols and write 'T'
;accept2: write 'r'
;accept3: write 'u'
;accept4: write 'e'
;halt_accept: final accept state
;reject: move to thr rightmost
;reject1: eliminate all non-blank symbols and write 'F'
;reject2: write 'a'
;reject3: write 'l'
;reject4: write 's'
;reject5: write 'e'
;halt_reejct: final reject state

;transition functions
1 a c l 2
1 b c l 3
1 c c r 11
1 _ _ * accept

2 * a r 4
3 * b r 4
4 c c r 5

5 * * r 5
5 _ _ l 6
5 c c l 6

6 a c r 7
6 b c r 8
6 c c r reject

7 * a l 9
8 * b l 9
9 c c l 10
10 * * l 10
10 c c r 1

11 c c r 11
11 a c l 12
11 b c l 13
11 _ _ l 17

12 * * l 12
12 _ _ r 14
13 * * l 13
13 _ _ r 15
14 a _ r 16
14 b b r reject
15 b _ r 16
15 a a r reject
16 * * r 16
16 c c r 11

17 c c l 17
17 _ _ r accept

accept * _ r accept
accept _ T r accept2
accept2 _ r r accept3
accept3 _ u r accept4
accept4 _ e * halt_accept

reject * * r reject
reject _ _ l reject1
reject1 * _ l reject1
reject1 _ F r reject2
reject2 _ a r reject3
reject3 _ l r reject4
reject4 _ s r reject5
reject5 _ e * halt_reject
