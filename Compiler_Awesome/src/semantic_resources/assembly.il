MOV SP D0
ADD SP #1 SP
ADD SP #2 SP
ADD SP #2 SP
RD 4(D0)                   ;N
PUSH #1
POP 2(D0)                   ;I
L1:
WRT 2(D0)                   ;I
ADD 2(D0) #1 2(D0)
PUSH 2(D0)                   ;I
PUSH 4(D0)                   ;N
CMPNES
BRTS L1
WRTLN #""
POP D0
HLT
