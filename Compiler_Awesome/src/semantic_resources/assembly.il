MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
RD 3(D0)                   ;N
PUSH #1
POP 2(D0)                   ;I
PUSH #0
POP 1(D0)                   ;Sum
L1:
PUSH 2(D0)                   ;I
PUSH 3(D0)                   ;N
CMPLES
PUSH 2(D0)                   ;I
PUSH #100
CMPGTS
ORS
BRTS L2
BR L3
L2:
PUSH 1(D0)                   ;Sum
PUSH 2(D0)                   ;I
ADDS
POP 1(D0)                   ;Sum
PUSH 2(D0)                   ;I
PUSH #1
ADDS
POP 2(D0)                   ;I
WRT 2(D0)                   ;I
WRT 1(D0)                   ;Sum
BR L1
L3:
WRTLN #""
POP D0
HLT
