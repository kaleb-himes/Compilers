program Tester;

 var I: Integer;

{procedure this (VAR example:integer x)}

  begin {tester}
    Writeln('hi'); Writeln('THis is a crazy string for testing purposes.');
    Write('Please enter an integer value for I: ');
    Read(I);
    I := I + 1;

    FOR I:=5 to 12 do
        If I <= 6 then
            Write ( 'this stuff');
        else
            Write ( 'that stuff') ;
        I := I + 1;

    Writeln('The current value of I is ', I=0);
    Writeln(''); 
    Writeln('');
  end. {tester}
