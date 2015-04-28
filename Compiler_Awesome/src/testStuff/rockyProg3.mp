program Program1;    
  var N : Integer;
  
  procedure Fred;
    var I: Integer;
        J: Integer;
    begin
      J := 5;
      I := N;
      I := I + 1 + J;
      N := I;
    end;
    
  begin     
    Read(N);
    Write(N);
    Write('\n');
    Fred;
    Write(N)
  end.