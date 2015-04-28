MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
BR L0
L1:
MOV SP D1
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
PUSH #5
POP 2(D1)                   ;J
PUSH 1(D0)                   ;N
POP 1(D1)                   ;I
PUSH 1(D1)                   ;I
PUSH #1
PUSH 2(D1)                   ;J
ADDS
ADDS
POP 1(D1)                   ;I
PUSH 1(D1)                   ;I
POP 1(D0)                   ;N
POP D1
MOV SP D0
BR L501
L0:
RD 1(D0)                   ;N
WRT 1(D0)                   ;N
WRT #" \n"
BR L1                   ;Fred
L501:
WRT 1(D0)                   ;N
WRTLN #""
POP D0
HLT
