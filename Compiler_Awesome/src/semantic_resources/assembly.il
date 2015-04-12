MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
MOV #0 1(D0)                   ;Sum
ADD SP #1 SP
MOV #0 2(D0)                   ;I
ADD SP #1 SP
MOV #0 3(D0)                   ;N
RD 3(D0)                   ;N
PUSH 2(D0)                   ;I
PUSH #1
ADDS
POP 2(D0)                   ;I
PUSH 1(D0)                   ;Sum
PUSH #0
ADDS
POP 1(D0)                   ;Sum
CMPLES
BRFS L1
PUSH #100
CMPGTS
BRFS L1
PUSH 1(D0)                   ;Sum
PUSH 1(D0)                   ;Sum
PUSH 2(D0)                   ;I
ADDS
POP 1(D0)                   ;Sum
PUSH 2(D0)                   ;I
PUSH 2(D0)                   ;I
PUSH #1
ADDS
POP 2(D0)                   ;I
WRT 2(D0)                   ;I
WRT 1(D0)                   ;Sum
WRTLN #""
POP D0
HLT
