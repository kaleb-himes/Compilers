MOV SP D0
ADD SP #1 SP
MOV #0 1(D0)                   ;Apples
ADD SP #1 SP
MOV #0 2(D0)                   ;Fred
ADD SP #1 SP
MOV #0 3(D0)                   ;X
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
WRT #" Apples =  "
WRT 1(D0)                   ;Apples
WRT #" ; X =  "
WRT 3(D0)                   ;X
WRT #" and Fred =  "
WRT 2(D0)                   ;Fred
WRTLN #""
POP D0
HLT
