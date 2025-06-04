/*
Considere um sistema onde quatro drones sobrevoam determinados ambientes (divididos em leste,
oeste, norte e sul) e por meio de sensores, coletam dados de importantes elementos climáticos como
pressão atmosférica, radiação solar, temperatura e umidade.

- Drone norte coleta os dados no formato: pressao-radiacao-temperatura-umidade.
- Drone sul coleta os dados no formato: (pressao;radiacao;temperatura;umidade).
- Drone leste coleta os dados no formato: {pressao,radiacao,temperatura,umidade}.
- Drone oeste coleta os dados no formato: pressao#radiacao#temperatura#umidade.

*/

import java.lang.Math;

enum DroneType {
    NORTE, SUL, LESTE, OESTE
}

public class Drone {
    private DroneType tipo;

    public Drone(DroneType tipo) {
        this.tipo = tipo;
    }

    public Float getDados() {
        Float pressao = math.random() * 1000;
         Float radiacao = math.random() * 100;
         Float temperatura = math.random() * 40;
         Float umidade = math.random() * 100;
    }

    static void main(String[] args) {
    }

}