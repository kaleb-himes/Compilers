MOV SP D0
ADD SP #1 SP
ADD SP #2 SP
L0:
PUSH #1
POP 1(D0)                   ;a
PUSH #10
POP 2(D0)                   ;b
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPEQS
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPLTS
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPGTS
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPLES
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPGES
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPNES
WRTS
PUSH #"\n"
WRTS
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPLTS
BRFS L736
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L737
L736:
PUSH 2(D0)                   ;b
WRTS
PUSH #"\n"
WRTS
L737:
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPGTS
BRFS L738
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L739
L738:
PUSH 2(D0)                   ;b
WRTS
PUSH #"\n"
WRTS
L739:
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPLES
BRFS L740
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L741
L740:
PUSH 2(D0)                   ;b
WRTS
PUSH #"\n"
WRTS
L741:
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPGES
BRFS L742
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L743
L742:
PUSH 2(D0)                   ;b
WRTS
PUSH #"\n"
WRTS
L743:
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPEQS
BRFS L744
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L745
L744:
PUSH 2(D0)                   ;b
WRTS
PUSH #"\n"
WRTS
L745:
PUSH 1(D0)                   ;a
PUSH 2(D0)                   ;b
CMPNES
BRFS L746
PUSH 1(D0)                   ;a
WRTS
PUSH #"\n"
WRTS
BR L747
L746:
PUSH 2(D0)                   ;b
WRTS
PUSH #"\n"
WRTS
L747:
PUSH #1
PUSH #8
PUSH #8
CMPGTS
ADDS
BRFS L748
PUSH #1
WRTS
PUSH #"\n"
WRTS
BR L749
L748:
PUSH #0
WRTS
PUSH #"\n"
WRTS
L749:
PUSH #20
PUSH #4
MODS
PUSH #5
PUSH #0
CMPEQS
MULS
BRFS L750
PUSH #1
WRTS
PUSH #"\n"
WRTS
BR L751
L750:
PUSH #0
WRTS
PUSH #"\n"
WRTS
L751:
PUSH #20
PUSH #4
ADDS
PUSH #3
PUSH #3
ADDS
PUSH #10000
CMPGTS
PUSH #63
PUSH #14
ADDS
BRFS L752
PUSH #1
WRTS
PUSH #"\n"
WRTS
BR L753
L752:
PUSH #0
WRTS
PUSH #"\n"
WRTS
L753:
WRTLN #""
POP D0
HLT
