MOV SP D0
ADD SP #1 SP
ADD SP #1 SP
ADD SP #2 SP
ADD SP #1 SP
ADD SP #2 SP
L0:
PUSH #" This is test 7 of level C  "
WRTS
PUSH #"\n"
WRTS
PUSH #" This will further test reading and math  "
WRTS
PUSH #"\n"
WRTS
PUSH #"  "
WRTS
PUSH #"\n"
WRTS
PUSH #" Please enter 2 integers  "
POP 1(D0)                   ;aString
PUSH #"  "
WRTS
PUSH 1(D0)                   ;aString
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"\n"
WRTS
RD 2(D0)                   ;int1
RD 3(D0)                   ;int2
PUSH 2(D0)                   ;int1
WRTS
PUSH #" *  "
WRTS
PUSH 3(D0)                   ;int2
WRTS
PUSH #" =  "
WRTS
PUSH 2(D0)                   ;int1
PUSH 3(D0)                   ;int2
MULS
WRTS
PUSH #"\n"
WRTS
PUSH #"  "
WRTS
PUSH #"\n"
WRTS
PUSH #" Please enter 2 floats  "
POP 1(D0)                   ;aString
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH #"  "
WRTS
PUSH 1(D0)                   ;aString
WRTS
PUSH #"\n"
WRTS
RD 5(D0)                   ;f1
RD 6(D0)                   ;f2
PUSH 5(D0)                   ;f1
WRTS
PUSH #" *  "
WRTS
PUSH 6(D0)                   ;f2
WRTS
PUSH #" =  "
WRTS
PUSH 5(D0)                   ;f1
PUSH 6(D0)                   ;f2
MULS
WRTS
PUSH #"\n"
WRTS
PUSH #"  "
WRTS
PUSH #"\n"
WRTS
PUSH #" Now to double your first flaot and integer  "
WRTS
PUSH #"\n"
WRTS
PUSH 5(D0)                   ;f1
PUSH 5(D0)                   ;f1
MULS
POP 5(D0)                   ;f1
PUSH 2(D0)                   ;int1
PUSH 2(D0)                   ;int1
MULS
POP 2(D0)                   ;int1
PUSH 5(D0)                   ;f1
WRTS
PUSH #" and  "
WRTS
PUSH 2(D0)                   ;int1
WRTS
PUSH #"\n"
WRTS
PUSH #"  "
WRTS
PUSH #"\n"
WRTS
PUSH #" Now to increase your second integer by your second float and vice versa  "
WRTS
PUSH #" (should come out the same value)  "
WRTS
PUSH #"\n"
WRTS
PUSH 3(D0)                   ;int2
PUSH 6(D0)                   ;f2
MULS
POP 2(D0)                   ;int1
PUSH 3(D0)                   ;int2
PUSH 6(D0)                   ;f2
MULS
POP 6(D0)                   ;f2
PUSH 2(D0)                   ;int1
POP 3(D0)                   ;int2
PUSH 6(D0)                   ;f2
WRTS
PUSH #" and  "
WRTS
PUSH 3(D0)                   ;int2
WRTS
PUSH #"  "
WRTS
PUSH #"\n"
WRTS
WRTLN #""
POP D0
HLT
