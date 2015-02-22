program Tester;

 var I: Integer;

{procedure this (VAR example:integer x)}
  begin {tester}
    Writeln; Writeln;
    Write('Please enter an integer value for I: ');
    Read(I);
    I := I + 1;
    Writeln('The current value of I is ', I:0);
    Writeln; Writeln
  end. {tester}
