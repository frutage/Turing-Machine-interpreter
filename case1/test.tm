; This Turing Machine checks whether teh input string is of the form of 1^mx1^n=1^(mn) where m >= 1 and n >= 1

; the finite set of states
#Q = {start,A,B,C,D,E,F,G,H,I,K,check1,check2,check3,check4,accept,accept2,accept3,accept4,halt_accept,reject,reject1,reject2,reject3,reject4,reject5,halt_reject}

; the meaning for each state
;start: check the first '1'
;check1: check the first 1-string
;check2: check the second 1-string
;check3: check the third 1-string
;check4: return to the left
;A: deliminate the leftmost '1'
;B: move right until it meets 'x'
;C: mark the first '1' in the second 1-string as '0'
;D: move right until it meets blank
;E: eliminate the rightmost '1'
;F: move left until it meets mark '0'
;G: move back to the left
;H: the first 1-string is empty
;I: check the third 1-string empty
;K: check n > 0
;accept: eliminate all non-blank char and type 'T'
;accept2: type 'r'
;accept3: type 'u'
;accept4: type 'e'
;halt_accept: final accept state
;reject: move to thr rightmost
;reject1: eliminate all non-blank symbols and write 'F'
;reject2: write 'a'
;reject3: write 'l'
;reject4: write 's'
;reject5: write 'e'
;halt_reejct: final reject state

; the finite set of input symbols
#S = {1,x,=}

; the set of tape symbols
#T = {0,1,x,=,_,T,r,u,e,F,a,l,s}

; the start state
#q0 = start ;start state

; the blank symbol
#B = _

; the set of final states
#F = {halt_accept}

; transition functions
; start: read the first '1'
start 1 1 * check1
start * * * reject

; check whether it's the form as "1*x1*=1*"
check1 1 1 r check1
check1 x x r check2
check1 * * * reject
check2 1 1 r check2
check2 = = r check3
check2 * * * reject
check3 1 1 r check3
check3 _ _ l check4
check3 * * * reject
check4 * * l check4
check4 _ _ r A

; check whether it satisfies m*n
; state A
A 1 _ r B
A x x r H
A * * * reject
; state B
B 1 1 r B
B x x r K
B * * * reject
; state C
C = = l G
C 1 0 r D
C * * * reject
; state D
D * * r D
D _ _ l E
; state E
E 1 _ l F
E * * * reject
;state F
F * * l F
F 0 1 r C
; state G
G * * l G
G _ _ r A
; state H
H * * r H
H = = r I
; state I
I _ _ l accept
I * * * reject
; state K
K 1 1 * C
K * * * reject

; Write "True" when it accepts
accept * _ l accept
accept _ T r accept2
accept2 _ r r accept3
accept3 _ u r accept4
accept4 _ e * halt_accept

; Write "False" when it rejects
reject * * r reject
reject _ _ l reject1
reject1 * _ l reject1
reject1 _ F r reject2
reject2 _ a r reject3
reject3 _ l r reject4
reject4 _ s r reject5
reject5 * e * halt_reject
