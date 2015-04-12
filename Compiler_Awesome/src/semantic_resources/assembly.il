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
L1:
PUSH 2(D0)                   ;I
PUSH 3(D0)                   ;N
CMPLES
BRTS L2
PUSH 2(D0)                   ;I
PUSH #100
CMPGTS
BRTS L2
BR L3
L2:
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
WRT #"\n"
WRT 1(D0)                   ;Sum
WRT #"\n"
BR L1
L3:
WRTLN #""
POP D0
HLT
