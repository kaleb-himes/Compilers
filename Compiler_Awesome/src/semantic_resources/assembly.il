MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #4 SP
BR L2
L1:
ADD SP #0 SP
ADD SP #0 SP
BR L3
L2:
RD 3(D2)                   ;someBool
BR L4
L3:
RD 4(D1)                   ;somestuff
BR L5
L4:
ADD SP #0 SP
ADD SP #0 SP
RD 4(D1)                   ;somestuff
PUSH #5
POP DEFAULT_GET_OFFSET(D1)                   ;DEFAULT
ADD SP #0 SP
RD 4(D1)                   ;somestuff
ADD SP #0 SP
RD 4(D1)                   ;somestuff
WRT #" hi  "
WRT #" THis is a crazy string for testing purposes.  "
WRT #" Please enter an integer value for I:  "
RD 1(D0)                   ;I
PUSH 1(D0)                   ;I
PUSH #1
ADDS
POP 1(D0)                   ;I
PUSH #5
POP 1(D0)                   ;I
PUSH #12
L5:
PUSH 1(D0)                   ;I
PUSH #6
CMPGES
BRFS L6
WRT #" this stuff  "
BR L7
L6:
WRT #" that stuff  "
L7:
ADD 1(D0) #1 1(D0)
PUSH 1(D0)                   ;I
PUSH 1(D0)                   ;I
CMPNES
BRTS L5
PUSH 1(D0)                   ;I
PUSH #1
ADDS
POP 1(D0)                   ;I
PUSH #5
POP 1(D0)                   ;I
PUSH #12
L7:
PUSH 1(D0)                   ;I
PUSH #6
CMPLES
BRFS L8
WRT #" this stuff  "
BR L9
L8:
WRT #" that stuff  "
L9:
ADD 1(D0) #1 1(D0)
PUSH 1(D0)                   ;I
PUSH 1(D0)                   ;I
CMPNES
BRTS L7
PUSH 1(D0)                   ;I
PUSH #1
ADDS
POP 1(D0)                   ;I
PUSH 1(D0)                   ;I
PUSH #6
CMPLES
BRFS L9
WRT #" this stuff  "
L9:
PUSH 1(D0)                   ;I
PUSH #20
CMPNES
BR L11
L10:
PUSH 1(D0)                   ;I
PUSH #1
ADDS
POP 1(D0)                   ;I
BR L9
L11:
L12:
PUSH #5
POP 1(D0)                   ;I
PUSH 1(D0)                   ;I
PUSH #5
CMPNES
BR L12
L13:
WRT #" The current value of I is  "
WRTLN 1(D0)                   ;I
EQUALS
PUSH #0
WRT #"  "
WRT #"  "
WRTLN #""
POP D0
HLT
