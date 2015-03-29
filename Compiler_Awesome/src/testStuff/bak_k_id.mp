program Tester;

var I: Boolean;
someFloat: Float;
someBool: Boolean;
somestuff: Integer;
x, y, balloon, legcramp: String;

    procedure junk1 (VAR test:Float; VAR garbage,blob,plop:Boolean);
    begin
        read(somestuff);
    end;

    function junk2 (test:Float; garbage:Boolean) : Boolean;
    begin
        read(somestuff);
        junk2 := 5;
    end;

    function junk3 (VAR test:Float) : STRING;
    begin
        read(somestuff)
    end;

    function junk4 (VAR test:Float) : FLOAT;
    begin
        read(somestuff)
    end;

{procedure this (VAR example:integer x, Var garbage:FLOAT y)
commented with MP_INTEGER this line out from a previous test file}

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

        I := junk2(someFloat, someBool);

    While (I <> 20) do
        I := I + 1;

    Repeat
        I := 5
        until (I <> 5);

    Writeln('The current value of I is ', I = 0);
    Writeln(''); 
    Writeln('');
END.