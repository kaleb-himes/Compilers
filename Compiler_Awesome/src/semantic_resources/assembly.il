MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
MOV #0 1(D0)                   ;Apples
ADD SP #1 SP
MOV #0 2(D0)                   ;Cost
RD 1(D0)                   ;Apples
PUSH 1(D0)                   ;Apples
PUSH #0
CMPGTS
BRFS L1
PUSH 2(D0)                   ;Cost
PUSH 1(D0)                   ;Apples
PUSH #5
MULS
POP 2(D0)                   ;Cost
WRT 2(D0)                   ;Cost
BR L2
L1:
PUSH 2(D0)                   ;Cost
PUSH 1(D0)                   ;Apples
PUSH #7
MULS
POP 2(D0)                   ;Cost
WRT 2(D0)                   ;Cost
L2:
WRTLN #""
POP D0
HLT
