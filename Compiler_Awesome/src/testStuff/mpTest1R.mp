{ a sample program 1R }
program sample1R;
var i: integer;
      j: real;
begin
  readln(i); { get i }
  j := 9 + i * 8.3e3; { evaluate j }
  write( 'Result is ' );
  write( j ); { print j }
  writeln
end.