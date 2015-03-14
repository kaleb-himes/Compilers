program Tester;

 var I: Integer;

    procedure junk1 (VAR test:Float; VAR garbage,blob,plop:Boolean);
    begin
        read(somestuff)
    end; {tester}

    function junk2 (VAR test:Float; VAR garbage:Boolean) : Boolean;
    begin
        read(somestuff)
    end; {tester}

    function junk3 (VAR test:Float) : STRING;
    begin
        read(somestuff)
    end; {tester}

    function junk4 (VAR test:Float) : FLOAT;
    begin
        read(somestuff)
    end; {tester}

{procedure this (VAR example:integer x, Var garbage:FLOAT y)
commented with MP_INTEGER this line out from a previous test file}

  begin {tester}
    Writeln('hi'); Writeln('THis is a crazy string for testing purposes.');
    Write('Please enter an integer value for I: ');
    Read(I);
    A := A + 1;

    FOR B:=5 to 12 do
        If B >= 6 then
            Write ( 'this stuff')
        else
            Write ( 'that stuff');
        B := B + 1;

    FOR C:=5 downto 12 do
        If C <= -6 then
            Write ( 'this stuff') {just in case}
        else
            Write ( 'that stuff');
        C := C + 1;
        If C <= -6 then
            Write ( 'this stuff');

    While (D <> 20) do
        D := D + 1;

    Repeat
        E := 5
        until (E <> 5);

    Writeln('The current value of F is ', F = 0);
    Writeln(''); 
    Writeln('');
END.