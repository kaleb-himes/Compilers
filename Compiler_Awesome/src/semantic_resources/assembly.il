MOV SP D0
ADD SP #1 SP
ADD SP #3 SP
MOV #0 1(D0)                   ;Sum
MOV #0 2(D0)                   ;I
MOV #0 3(D0)                   ;N
RD 1(D0)                   ;N
PUSH 1(D0)                   ;I
PUSH #0
ADDS
POP 1(D0)                   ;I
PUSH 1(D0)                   ;Sum
PUSH #0
ADDS
POP 1(D0)                   ;Sum
PUSH 1(D0)                   ;Sum
PUSH 1(D0)                   ;Sum
PUSH 1(D0)                   ;I
ADDS
POP 1(D0)                   ;Sum
PUSH 1(D0)                   ;I
PUSH 1(D0)                   ;I
PUSH #1
ADDS
POP 1(D0)                   ;I
WRT 1(D0)                   ;I
WRT 1(D0)                   ;Sum
CMPGTS
BRFS L1
PUSH #100
CMPLES
BRFS L1
WRTLN #""
POP D0
HLT
