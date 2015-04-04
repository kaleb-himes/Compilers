program Program1;
  var A : integer;
        B: float;
  someString : String;
  tempfloat: float;
  tempbool: Boolean;

    function funct(somefloat: float; somebool: Boolean) : String;
    begin {funct}
        read(someString);
        write('found the string');
        funct := someString;
    end; {funct}

    begin {Program 1}
      A := funct(tempbool, B);
      Write('A = ', A);
    end.