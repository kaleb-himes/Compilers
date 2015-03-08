program Tester;

 var I: Integer;

    procedure junk (VAR test:Float; VAR garbage,junk,plop:Boolean);
    begin
        read(somestuff)
    end; {tester}

    function junk (VAR test:Float; VAR garbage:Boolean) : Boolean;
    begin
        read(somestuff)
    end; {tester}

    function junk (VAR test:Float) : STRING;
    begin
        read(somestuff)
    end; {tester}

    function junk (VAR test:Float) : FLOAT;
    begin
        read(somestuff)
    end; {tester}

{procedure this (VAR example:integer x, Var garbage:FLOAT y)}
  begin {tester}
    Writeln('hi'); Writeln('THis is a crazy string for testing purposes.');
    Write('Please enter an integer value for I: ');
    Read(I);
    I := I + 1;

    FOR I:=5 to 12 do
        If I >= 6 then
            Write ( 'this stuff')
        else
            Write ( 'that stuff');
        I := I + 1;

    FOR I:=5 downto 12 do
        If I <= -6 then
            Write ( 'this stuff') {just in case}
        else
            Write ( 'that stuff');
        I := I + 1;
        If I <= -6 then
            Write ( 'this stuff');

    While (I <> 20) do
        I := I + 1;

    Repeat
        I := 5
        until (I <> 5);

    Writeln('The current value of I is ', I = 0);
    Writeln(''); 
    Writeln('');
END.