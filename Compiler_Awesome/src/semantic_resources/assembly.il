MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #1 SP
3(D0)                 ;Apples
ASSIGN-TO
#0
var(D0)                 ;X
ASSIGN-TO
#10
DEFAULT(D0)                 ;Fred
ASSIGN-TO
2(D0)                 ;Apples
PLUS
1(D0)                 ;Apples
MULTIPLY
1(D0)                 ;X
WRT -> WRT -> 3(D0)                 ;Apples
WRT -> WRT -> 1(D0)                 ;X
WRT -> WRT -> 3(D0)                 ;Fred
POP D0
HLT
