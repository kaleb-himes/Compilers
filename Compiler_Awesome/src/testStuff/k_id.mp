program Program1;
  var A : integer;
        B: float;
  someInt : Integer;
  tempfloat: float;
  tempbool: Boolean;

    function funct(somefloat: float; somebool: Boolean) : Integer;
    begin {funct}
        read(someInt);
        write('found the Integer');
        funct := someInt;
    end; {funct}

    begin {Program 1}
      A := funct(B, tempbool) * A;
      Write('A = ', A);
    end.