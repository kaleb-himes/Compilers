MOV SP D0
ADD SP #1 SP
ADD SP #2 SP
L0:
RD 1(D0)                   ;a
RD 2(D0)                   ;b
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
L589:
CMPGTS
BRTS L591
L590:
PUSH 1(D0)                   ;a
PUSH #1
SUBS
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L589
L591:
PUSH #20
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
L592:
CMPGES
BRTS L594
L593:
PUSH 1(D0)                   ;a
PUSH #1
SUBS
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L592
L594:
PUSH #0
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
L595:
CMPLTS
BRTS L597
L596:
PUSH 1(D0)                   ;a
PUSH #1
ADDS
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L595
L597:
PUSH #0
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
L598:
CMPLES
BRTS L600
L599:
PUSH 1(D0)                   ;a
PUSH #1
ADDS
POP 1(D0)                   ;a
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L598
L600:
WRTLN #""
POP D0
HLT
