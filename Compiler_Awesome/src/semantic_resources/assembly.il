MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
MOV #0 1(D0)                   ;I
ADD SP #1 SP
MOV #0 2(D0)                   ;N
RD 2(D0)                   ;N
PUSH 1(D0)                   ;I
PUSH #1
ADDS
POP 1(D0)                   ;I
L1:
WRT 1(D0)                   ;I
ADD 1(D0) #1 1(D0)
PUSH 1(D0)                   ;I
PUSH 2(D0)                   ;N
CMPNES
BRTS L1
WRTLN #""
POP D0
HLT
