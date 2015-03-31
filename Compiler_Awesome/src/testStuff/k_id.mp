program Program1;
  var A : integer;
  someString : String;
  tempfloat: float;
  tempbool: Boolean;

    function funct(somefloat: float; somebool: Boolean) : String;
    begin {funct}
        read(someString);
        write('found the string');
        funct := someString;
    end; {funct}

    begin
      A := 1;
      someString := funct(tempfloat, tempbool);
      Write('A = ', A)
    end.