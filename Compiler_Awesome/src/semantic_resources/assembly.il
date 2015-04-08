MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
0(D0)                 ;Apples
ASSIGN-TO
PUSH #0
1(D0)                 ;X
ASSIGN-TO
PUSH #10
2(D0)                 ;Fred
ASSIGN-TO
3(D0)                 ;Apples
PLUS
4(D0)                 ;Apples
MULTIPLY
5(D0)                 ;X
WRT -> WRT -> 6(D0)                 ;Apples
WRT -> WRT -> 7(D0)                 ;X
WRT -> WRT -> 8(D0)                 ;Fred
POP D0
HLT
