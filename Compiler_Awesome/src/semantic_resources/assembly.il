MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
L0:
RD 2(D0)                   ;N
PUSH #1
POP 1(D0)                   ;I
L442:
PUSH 1(D0)                   ;I
WRTS
PUSH #"\n"
WRTS
ADD 1(D0) #1 1(D0)
PUSH 1(D0)                   ;I
PUSH 2(D0)                   ;N
CMPNES
BRTS L442
PUSH #20
POP 1(D0)                   ;I
L443:
PUSH 1(D0)                   ;I
WRTS
PUSH #"\n"
WRTS
SUB 1(D0) #1 1(D0)
PUSH 1(D0)                   ;I
PUSH 2(D0)                   ;N
CMPNES
BRTS L443
WRTLN #""
POP D0
HLT
