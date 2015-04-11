MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
MOV #0 1(D0)                 ;Apples
PUSH 1(D0)                   ;Apples
MOV 
MOV #10 3(D0)                 ;X
PUSH 3(D0)                   ;X
MOV 
PLUS
MULTIPLY
2(D0)                 ;Fred
PUSH 2(D0)                   ;Fred
MOV 1(D0)                 ;Apples
PUSH 1(D0)                   ;Apples
1(D0)                 ;Apples
PUSH 1(D0)                   ;Apples
3(D0)                 ;X
PUSH 3(D0)                   ;X

WRT -> WRT -> WRT -> WRT -> WRT -> WRT -> POP D0
HLT
