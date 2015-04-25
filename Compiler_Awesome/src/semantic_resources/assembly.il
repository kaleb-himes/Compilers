MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
PUSH 1(D0)                   ;N
PUSH #1
ADDS
POP 1(D0)                   ;N
BR L2
L1:
RD 1(D0)                   ;N
WRTLN 1(D0)                   ;N
PUSH null(D0)                   ;Fred
WRT 1(D0)                   ;N
WRTLN #""
POP D0
HLT
