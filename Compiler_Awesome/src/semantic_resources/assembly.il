MOV SP D0
ADD SP #1 SP
ADD SP #6 SP
ADD SP #3 SP
L0:
PUSH #1
POP 1(D0)                   ;a
PUSH #0
POP 2(D0)                   ;b
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
ANDS
POP 3(D0)                   ;c
PUSH #" a and b =  "
WRTS
PUSH 3(D0)                   ;c
WRTS
PUSH #" : should be FALSE  "
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
ORS
POP 3(D0)                   ;c
PUSH #" a or b =  "
WRTS
PUSH 3(D0)                   ;c
WRTS
PUSH #" : should be TRUE  "
WRTS
PUSH #"\n"
WRTS
PUSH 2(D0)                   ;b
NOTS
POP 3(D0)                   ;c
PUSH #" not b =  "
WRTS
PUSH 3(D0)                   ;c
WRTS
PUSH #" : should be TRUE  "
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
NOTS
NOTS
POP 3(D0)                   ;c
PUSH #" not not a =  "
WRTS
PUSH 3(D0)                   ;c
WRTS
PUSH #" : should be TRUE  "
WRTS
PUSH #"\n"
WRTS
PUSH #1
POP 7(D0)                   ;x
PUSH #2
POP 8(D0)                   ;y
PUSH #3
POP 9(D0)                   ;z
PUSH 7(D0)                   ;x
PUSH 8(D0)                   ;y
CMPLTS
PUSH 8(D0)                   ;y
PUSH 9(D0)                   ;z
CMPLTS
ANDS
POP 3(D0)                   ;c
PUSH #" x =  "
WRTS
PUSH 7(D0)                   ;x
WRTS
PUSH #" , y =  "
WRTS
PUSH 8(D0)                   ;y
WRTS
PUSH #" , z =  "
WRTS
PUSH 9(D0)                   ;z
WRTS
PUSH #" , and c =  "
WRTS
PUSH 3(D0)                   ;c
WRTS
PUSH #" : should be TRUE  "
WRTS
PUSH #"\n"
WRTS
WRTLN #""
POP D0
HLT
