MOV SP D0
ADD SP #1 SP
ADD SP #3 SP
MOV #0 1(D0)                   ;Sum
MOV #0 2(D0)                   ;I
MOV #0 3(D0)                   ;N
RD 3(D0)                   ;N
PUSH #0
POP 2(D0)                   ;I
PUSH #0
POP 1(D0)                   ;Sum
L1:
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
CMPGTS
BRFS L2
PUSH #100
CMPLES
BRFS L2
WRTLN #""
POP D0
HLT
