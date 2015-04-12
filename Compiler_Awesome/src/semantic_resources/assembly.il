MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
PUSH 1(D0)                   ;Apples
PUSH #0
ADDS
PUSH 3(D0)                   ;X
PUSH #10
ADDS
PUSH 2(D0)                   ;Fred
PUSH 1(D0)                   ;Apples
PUSH 1(D0)                   ;Apples
PUSH 3(D0)                   ;X
MULS
ADDS
POP 2(D0)                   ;Fred
WRT -> WRT -> PUSH 1(D0)                   ;Apples
WRT -> WRT -> PUSH 3(D0)                   ;X
WRT -> WRT -> PUSH 2(D0)                   ;Fred
POP D0
HLT
