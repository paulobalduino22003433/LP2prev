package pt.ulusofona.lp2.deisichess;

public class GameResults {
    int jogadasSemCaptura;
    String resultadoJogo = "";

    public GameResults() {}

    public void incJogadasSemCaptura() {
        jogadasSemCaptura++;
    }

    public void setResultadoJogo(String resultadoJogo) {
        this.resultadoJogo = resultadoJogo;
    }

    public int getJogadasSemCaptura() {
        return jogadasSemCaptura;
    }

    public String getResultadoJogo() {
        return resultadoJogo;
    }
}

