MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
L0:
PUSH #0
POP 3(D0)                   ;c
L833:
PUSH 3(D0)                   ;c
PUSH #1
ADDS
POP 3(D0)                   ;c
PUSH 3(D0)                   ;c
WRTS
PUSH 3(D0)                   ;c
PUSH #10
CMPLES
BRTS L833
L834:
L589:
PUSH 3(D0)                   ;c
PUSH #0
CMPLES
BRTS L591
L590:
PUSH 3(D0)                   ;c
PUSH #1
SUBS
POP 3(D0)                   ;c
PUSH 3(D0)                   ;c
WRTS
BR L589
L591:
PUSH #4
POP 3(D0)                   ;c
PUSH #1
POP 4(D0)                   ;d
ADD SP #1 SP
PUSH 3(D0)                   ;c
POP 5(D0)                   ;temp_c
L442:
PUSH 4(D0)                   ;d
WRTS
PUSH 3(D0)                   ;c
WRTS
PUSH 3(D0)                   ;c
PUSH #1
ADDS
POP 3(D0)                   ;c
ADD 4(D0) #1 4(D0)
PUSH 4(D0)                   ;d
PUSH 5(D0)                   ;temp_c
CMPNES
BRTS L442
PUSH 3(D0)                   ;c
WRTS
PUSH #3
POP 1(D0)                   ;a
PUSH #4
POP 2(D0)                   ;b
PUSH #20
PUSH 2(D0)                   ;b
DIVS
POP 3(D0)                   ;c
PUSH 3(D0)                   ;c
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPGES
NOTS
BRTS L736
PUSH 1(D0)                   ;a
WRTS
L736:
WRTLN #""
POP D0
HLT
