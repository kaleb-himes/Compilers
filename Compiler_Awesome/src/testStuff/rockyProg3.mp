program Program2;
  var Apples : integer;
    Fred : integer;
    X : integer;
    Y : string;
  begin
    Read(Apples, X);
    Fred := X + X * Apples;
    Write('Apples = ', Apples, ' ; X = ', X,' and Fred = ', Fred)
  end.