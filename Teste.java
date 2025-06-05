import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Teste {
    public static void main(String args[]) {
        String str = "4(4)34(43)339(43(343)43)33";
        IntUnaryOperator ppp = c -> (c == '(' || c == ')' ? ' ' : c);
        
        String result = str.chars()
            .mapToObj(c -> (c == '(' || c == ')') ? ' ' : (char) c) // transforma cada char
            .map(String::valueOf) // converte cada char em String
            .collect(Collectors.joining()); // junta tudo numa string

        for (String a: result.split("\\s")) {
            System.out.println(a);
        }
    }
}
