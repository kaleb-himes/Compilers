program Program1;
var B : integer;
X : String;
A : Float;
begin
      B := X + A;   {error1 and error 2}
      A := B;       {error3}

      B := A + B + B; {error4}

      Write('B = ', B)
end.