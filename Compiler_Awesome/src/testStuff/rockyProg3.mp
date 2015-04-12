program Program1;
 var Apples: Integer;			{1(D0)}
       Cost:   Integer;				{2(D0)}
 begin
     Read(Apples);
     if Apples > 0
       then begin
         Cost := Apples * 5;
         Write(Cost)
       end
       else begin
         Cost := Apples * 7;
         Write(Cost)
       end
   end.