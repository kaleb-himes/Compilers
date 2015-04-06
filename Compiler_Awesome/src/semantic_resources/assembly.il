PUSH 0
PUSH SP
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
RD 0(null)                 ;N
1(null)                 ;I
ASSIGN-TO
PUSH #1
2(null)                 ;Sum
ASSIGN-TO
PUSH #0
3(null)                 ;I
LESS_THAN_OR_EQUAL_TO
4(null)                 ;N
OR
5(null)                 ;I
GREATER_THAN
PUSH #100
6(null)                 ;Sum
ASSIGN-TO
7(null)                 ;Sum
PLUS
8(null)                 ;I
9(null)                 ;I
ASSIGN-TO
10(null)                 ;I
PLUS
PUSH #1
WRT -> 11(null)                 ;I
WRT -> 12(null)                 ;Sum
POP D0
HLT
