package pt.ulusofona.lp2.deisichess;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    ArrayList<Peca> pecas = new ArrayList<>();
    String[][] cordenadasPecasArray;
    ArrayList<Peca> blackTeam = new ArrayList<>();
    ArrayList<Peca> whiteTeam = new ArrayList<>();
    Tabuleiro tabuleiro = new Tabuleiro(whiteTeam, blackTeam);
    StatsPeca statusPreta = new StatsPeca();
    StatsPeca statusBranca = new StatsPeca();
    GameResults gameResults = new GameResults();


    void loadGame(File file) throws InvalidGameInputException, IOException {
        try {
            ArrayList<String> cordenadasPecas = new ArrayList<>();
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String linha;
            int pecasRestantes = 0;
            int numLine = 0;

            while ((linha = fileReader.readLine()) != null) {
                numLine++;

                if (tabuleiro.getTamanhoTabuleiro() == -1) {
                    tabuleiro.setTamanhoTabuleiro(Integer.parseInt(linha.trim()));
                    continue;
                }

                if (tabuleiro.getNumPecaTotal() == -1) {
                    tabuleiro.setNumPecaTotal(Integer.parseInt(linha.trim()));
                    continue;
                }

                if (pecasRestantes < tabuleiro.getNumPecaTotal()) {
                    String[] partes = linha.split(":");

                    if (partes.length != 4) {
                        throw new InvalidGameInputException(partes.length, numLine);
                    }

                    Peca peca = colocarTipoDePeca(partes[0].trim(), partes[1].trim(), partes[2].trim(), partes[3].trim());

                    pecas.add(peca);

                    pecasRestantes++;
                    continue;
                }

                cordenadasPecas.add(linha);
            }

            int linhas = tabuleiro.getTamanhoTabuleiro();
            int colunas = tabuleiro.getTamanhoTabuleiro();

            cordenadasPecasArray = new String[linhas][colunas];

            for (int i = 0; i < linhas; i++) {
                String[] parts = cordenadasPecas.get(i).split(":");

                for (int j = 0; j < colunas; j++) {
                    cordenadasPecasArray[i][j] = parts[j];
                }
            }

            setCoordinatesPieces();
            organizePiece();
            removeCapturedPieces();

            fileReader.close();

        } catch (InvalidGameInputException e) {
            System.out.println("Ocorreu um erro ao ler o ficheiro, na linha " + e.getLineWithError() + " com o seguinte problema: " + e.getProblemDescription());
        } catch (IOException e) {
            System.out.println("Exception: Ficheiro não existente");
        }
    }

    public Peca colocarTipoDePeca(String identificador, String tipoDePeca, String equipa, String alcunha) {
        Peca pecaDeRetorno = switch (tipoDePeca) {
            case "0" -> new PecaRei(identificador, tipoDePeca, equipa, alcunha);
            case "1" -> new PecaRainha(identificador, tipoDePeca, equipa, alcunha);
            case "2" -> new PecaPoneiMagico(identificador, tipoDePeca, equipa, alcunha);
            case "3" -> new PecaPadreVila(identificador, tipoDePeca, equipa, alcunha);
            case "4" -> new PecaTorreH(identificador, tipoDePeca, equipa, alcunha);
            case "5" -> new PecaTorreV(identificador, tipoDePeca, equipa, alcunha);
            case "6" -> new PecaHomer(identificador, tipoDePeca, equipa, alcunha);
            case "7" -> new PecaJoker(identificador, tipoDePeca, equipa, alcunha);
            default -> null;
        };

        return pecaDeRetorno;
    }

    public void setCoordinatesPieces() {
        for (int y = 0; y < tabuleiro.getTamanhoTabuleiro(); y++) {
            for (int x = 0; x < tabuleiro.getTamanhoTabuleiro(); x++) {
                for (int identificador = 0; identificador < tabuleiro.getNumPecaTotal(); identificador++) {
                    if (pecas.get(identificador).getIdentificador().equals(cordenadasPecasArray[y][x])) {
                        pecas.get(identificador).setX(Integer.toString(x));
                        pecas.get(identificador).setY(Integer.toString(y));
                    }
                }
            }
        }
    }

    public void organizePiece() {
        for (Peca peca : pecas) {
            if (peca.getEquipa().equals("10")) {
                blackTeam.add(peca);
            }
            if (peca.getEquipa().equals("20")) {
                whiteTeam.add(peca);
            }
        }
    }

    public void removeCapturedPieces() {
        Iterator<Peca> iterator = pecas.iterator();

        while (iterator.hasNext()) {
            Peca peca = iterator.next();
            if (peca.getX().isEmpty() || peca.getY().isEmpty()) {
                peca.estadoPecaCapturado();
                whiteTeam.remove(peca);
                blackTeam.remove(peca);
            }
        }
    }

    public int getBoardSize() {
        return tabuleiro.getTamanhoTabuleiro();
    }


    public String[] getSquareInfo(int x, int y) {
        String[] pecaInfo = new String[5];

        if(x<0 || y<0 || x>tabuleiro.getTamanhoTabuleiro() -1 || y>tabuleiro.getTamanhoTabuleiro() -1) {
            return null;
        }

        for (Peca peca : pecas) {
            if (peca.getX().equals(Integer.toString(x)) && peca.getY().equals(Integer.toString(y))) {
                pecaInfo[0] = peca.getIdentificador();
                pecaInfo[1] = peca.getTipoDePeca();
                pecaInfo[2] = peca.getEquipa();
                pecaInfo[3] = peca.getAlcunha();
                pecaInfo[4] = peca.getPng();
            }
        }

        if (pecaInfo[0] == null) {
            return new String[0];
        }

        return pecaInfo;
    }


    public String[] getPieceInfo(int ID) {
        String[] peca = new String[7];

        for (Peca pecaClone : pecas) {
            if (pecaClone.getIdentificador().equals(Integer.toString(ID))) {
                peca[0] = pecaClone.getIdentificador();
                peca[1] = pecaClone.getTipoDePeca();
                peca[2] = pecaClone.getEquipa();
                peca[3] = pecaClone.getAlcunha();
                peca[4] = pecaClone.getEstado();
                peca[5] = pecaClone.getX();
                peca[6] = pecaClone.getY();
            }
        }

        return peca;
    }


    public String getPieceInfoAsString(int ID) {
        return pecas.get(ID - 1).toString();
    }


  /*/public boolean isMoveValid(Peca peca,int x0,int y0, int x1, int y1) {
        boolean isItvalid = false;

        switch (peca.tipoDePeca) {
            case "0":
                if (x1 > x0 + 1 || y1 > y0 + 1) {
                    if(tabuleiro.getIsBlackTurn()){
                        statusPreta.incInvalidMoves();
                        isItvalid= false;
                    }
                    statusBranca.incInvalidMoves();
                    isItvalid= false;
                }else{
                    isItvalid= true;
                }
            case "1":
                if(x1>x0+5 || y1>y0+5){
                    if(tabuleiro.getIsBlackTurn()){
                        statusPreta.incInvalidMoves();
                        isItvalid= false;
                    }
                    statusBranca.incInvalidMoves();
                    isItvalid= false;
                }else{
                    isItvalid = true;
                }

            case "4":
                if(y1!=y0){
                    if(tabuleiro.getIsBlackTurn()){
                        statusPreta.incInvalidMoves();
                        isItvalid= false;
                    }
                    statusBranca.incInvalidMoves();
                    isItvalid= false;
                }else{
                    isItvalid=true;
                }
            case "5":
                if(x1!=x0){
                    if(tabuleiro.getIsBlackTurn()){
                        statusPreta.incInvalidMoves();
                        isItvalid = false;
                    }
                    statusBranca.incInvalidMoves();
                    isItvalid = false;
                }else{
                    isItvalid = true;
                }
        }
        return isItvalid;
    }/*/

    public ArrayList<String> possibilidadesDeMovimentos(int tipoPeca, int tamanhoTabuleiro, int turnoAtual, int x0, int y0) {
        ArrayList<String> output = new ArrayList<>();
        int limiteDoTabuleiro = (tamanhoTabuleiro - 1);

        if (x0 >= tamanhoTabuleiro || y0 >= tamanhoTabuleiro) {
            return null;
        } else if (x0 < 0 || y0 < 0) {
            return null;
        }

        switch (tipoPeca) {
            case 0 -> { //Rei
                if (x0 < limiteDoTabuleiro) {
                    output.add((x0 + 1) + "/" + y0);
                }
                if (x0 != 0) {
                    output.add((x0 - 1) + "/" + y0);
                }

                if (y0 < limiteDoTabuleiro) {
                    output.add(x0 + "/" + (y0 + 1));
                }
                if (y0 != 0) {
                    output.add(x0 + "/" + (y0 - 1));
                }

                if (x0 < limiteDoTabuleiro && y0 < limiteDoTabuleiro) {
                    output.add((x0 + 1) + "/" + (y0 + 1));
                }
                if (x0 < limiteDoTabuleiro && y0 != 0) {
                    output.add((x0 + 1) + "/" + (y0 - 1));
                }
                if (x0 != 0 && y0 < limiteDoTabuleiro) {
                    output.add((x0 - 1) + "/" + (y0 + 1));
                }
                if (x0 != 0 && y0 != 0) {
                    output.add((x0 - 1) + "/" + (y0 - 1));
                }
            }
            case 1 -> { //Rainha
                output.add(possibilidadesDeMovimentos(3, tamanhoTabuleiro, -1, x0, y0) + "");
                output.add(possibilidadesDeMovimentos(4, tamanhoTabuleiro, -1, x0, y0) + "");
                output.add(possibilidadesDeMovimentos(5, tamanhoTabuleiro, -1, x0, y0) + "");
            }
            case 2 -> { //Ponei Mágico
                if ((x0 + 1) < limiteDoTabuleiro) {
                    if ((y0 + 1) < limiteDoTabuleiro) {
                        output.add((x0 + 2) + "/" + (y0 + 2));
                    }
                    if ((y0 - 1) > 0 && (y0 - 1) < limiteDoTabuleiro) {
                        output.add((x0 + 2) + "/" + (y0 - 2));
                    }
                }

                if ((x0 - 1) > 0 && (x0 - 1) < limiteDoTabuleiro) {
                    if ((y0 + 1) < limiteDoTabuleiro) {
                        output.add((x0 - 2) + "/" + (y0 + 2));
                    }
                    if ((y0 - 1) > 0 && (y0 - 1) < limiteDoTabuleiro) {
                        output.add((x0 - 2) + "/" + (y0 - 2));
                    }
                }
            }
            case 3 -> { //Padre da Vila
                int limiteDeCasas = 3;

                if (turnoAtual == -1) {
                    limiteDeCasas = 5;
                }

                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((x0 + i) < limiteDoTabuleiro && (y0 + i) < limiteDoTabuleiro) {
                        output.add((x0 + i + 1) + "/" + (y0 + i + 1));
                    }
                }
                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((x0 - i) > 0 && (y0 - i) > 0) {
                        output.add((x0 - i - 1) + "/" + (y0 - i - 1));
                    }
                }

                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((y0 - i) <= 0) {
                        break;
                    }
                    if ((x0 + i) < limiteDoTabuleiro && (y0 - i) <= limiteDoTabuleiro) {
                        output.add((x0 + i + 1) + "/" + (y0 - i - 1));
                    }
                }
                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((x0 - i) <= 0) {
                        break;
                    }
                    if ((x0 - i) <= limiteDoTabuleiro && (y0 + i) < limiteDoTabuleiro) {
                        output.add((x0 - i - 1) + "/" + (y0 + i + 1));
                    }
                }
            }
            case 4 -> { //Torre Horizontal
                int limiteDeCasas = limiteDoTabuleiro;

                if (turnoAtual == -1) {
                    limiteDeCasas = 5;
                }

                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((x0 + i) < limiteDoTabuleiro) {
                        output.add((x0 + i + 1) + "/" + y0);
                    }
                }

                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((x0 - i) <= 0) {
                        break;
                    }
                    if ((x0 - i) <= limiteDoTabuleiro) {
                        output.add((x0 - i - 1) + "/" + y0);
                    }
                }
            }
            case 5 -> { //Torre Vertical
                int limiteDeCasas = limiteDoTabuleiro;

                if (turnoAtual == -1) {
                    limiteDeCasas = 5;
                }

                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((y0 + i) < limiteDoTabuleiro) {
                        output.add(x0 + "/" + (y0 + i + 1));
                    }
                }
                for (int i = 0; i < limiteDeCasas; i++) {
                    if ((y0 - i) <= 0) {
                        break;
                    }
                    if ((y0 - i) <= limiteDoTabuleiro) {
                        output.add(x0 + "/" + (y0 - i - 1));
                    }
                }
            }
            case 6 -> { //Homer
                if (turnoAtual % 3 == 0) {
                    return null;
                }

                if (x0 < limiteDoTabuleiro && y0 < limiteDoTabuleiro) {
                    output.add((x0 + 1) + "/" + (y0 + 1));
                }
                if (x0 > 0 && y0 > 0) {
                    output.add((x0 - 1) + "/" + (y0 - 1));
                }

                if (x0 < limiteDoTabuleiro && y0 <= limiteDoTabuleiro && (y0 - 1) >= 0) {
                    output.add((x0 + 1) + "/" + (y0 - 1));
                }
                if (x0 <= limiteDoTabuleiro && y0 < limiteDoTabuleiro && (x0 - 1) >= 0) {
                    output.add((x0 - 1) + "/" + (y0 + 1));
                }
            }
            case 7 -> { //Joker
                int pecaAtual = turnoAtual + 1;

                while (pecaAtual > 6) {
                    pecaAtual -= 6;
                }

                if (pecaAtual == 1) {
                    output.add(possibilidadesDeMovimentos(pecaAtual, tamanhoTabuleiro, -1, x0, y0) + "");
                } else {
                    output.add(possibilidadesDeMovimentos(pecaAtual, tamanhoTabuleiro, turnoAtual, x0, y0) + "");
                }
            }
        }

        System.out.println(output);
        return output;
    }

    public boolean verificarPossibilidadesDeMovimentos(Peca peca, int x1, int y1) {
        int turnoAtual = statusBranca.getValidMoves() + statusPreta.getValidMoves();
        int tipoPeca = Integer.parseInt(peca.getTipoDePeca().trim());
        int x0 = Integer.parseInt(peca.getX().trim());
        int y0 = Integer.parseInt(peca.getY().trim());
        int pecaAtualJoker = turnoAtual + 1;
        String[] partes;
        String auxiliar;
        ArrayList<String> movimentosParaPeca = possibilidadesDeMovimentos(tipoPeca, tabuleiro.getTamanhoTabuleiro(), turnoAtual, x0, y0);

        if (movimentosParaPeca == null) {
            return false;
        }

        while (pecaAtualJoker > 6) {
            pecaAtualJoker -= 6;
        }

        System.out.println(">- " + movimentosParaPeca.get(0));
        for (String s : movimentosParaPeca) {
            if (tipoPeca == 1 || (tipoPeca == 7 && pecaAtualJoker == 1)) { //Quando é a Rainha
                auxiliar = s;

                System.out.println("__" + auxiliar.length());
                for (int j = 0; j < auxiliar.length(); j++) {

                }
            }
            partes = s.split("/");

            System.out.println("(" + partes[0] + "," + partes[1] + ")");
        }

        return false;
    }

    public boolean veSePodeSeMovimentar(int tipoPeca, int x0, int y0, int x1, int y1) {
        boolean output = false;

        switch (tipoPeca) {
            case 0 -> {
            }
        }

        return output;
    }

    public boolean move(int x0, int y0, int x1, int y1) {
        //movimento do rei
        if (x1 > x0 + 1 || y1 > y0 + 1) {
            if(tabuleiro.isBlackTurn()){
                statusPreta.incInvalidMoves();
                return false;
            }
            statusBranca.incInvalidMoves();
            return false;
        }

        //movimento invalido (para qualquer peca)
        if (x1 < 0 || y1 < 0) {
            if(tabuleiro.isBlackTurn()){
                statusPreta.incInvalidMoves();
                return false;
            }
            statusBranca.incInvalidMoves();
            return false;
        }

        //movimento invalido (para qualquer peca)
        if (x1 > tabuleiro.getTamanhoTabuleiro() - 1 || y1 > tabuleiro.getTamanhoTabuleiro() - 1) {
            if(tabuleiro.isBlackTurn()){
                statusPreta.incInvalidMoves();
                return false;
            }
            statusBranca.incInvalidMoves();
            return false;
        }

        //movimento invalido (para qualquer peca)
        if (x0 == x1 && y0 == y1) {
            if(tabuleiro.isBlackTurn()){
                statusPreta.incInvalidMoves();
                return false;
            }
            statusBranca.incInvalidMoves();
            return false;
        }

        if (cordenadasPecasArray[y0][x0].equals("0")) {
            if(tabuleiro.isBlackTurn()){
                statusPreta.incInvalidMoves();
                return false;
            }
            statusBranca.incInvalidMoves();
            return false;
        }

        String pecaAtual = cordenadasPecasArray[y0][x0];
        String movimentoParaPeca = cordenadasPecasArray[y1][x1];
        boolean pecaCapturada = false;

        if (tabuleiro.isBlackTurn()) {
            //movimento invalido (para qualquer peca)
            for (Peca pecaBranca : whiteTeam) {
                if (pecaBranca.getIdentificador().equals(pecaAtual)) {
                    statusPreta.incInvalidMoves();
                    return false;
                }
            }

            //captura peca
            for (Peca pecaBranca : whiteTeam) {
                if (pecaBranca.getIdentificador().equals(movimentoParaPeca)) {
                    pecaBranca.estadoPecaCapturado();
                    pecaBranca.x = "";
                    pecaBranca.y = "";
                    whiteTeam.remove(pecaBranca);
                    pecaCapturada = true;
                    statusPreta.incCaptures();
                    break;
                }
            }
            statusPreta.incValidMoves();
        } else if (tabuleiro.isWhiteTurn()) {
            //movimento invalido (para qualquer peca)
            for (Peca pecaPreta : blackTeam) {
                if (pecaPreta.getIdentificador().equals(pecaAtual)) {
                    statusBranca.incInvalidMoves();
                    return false;
                }
            }

            //captura peca
            for (Peca pecaPreta : blackTeam) {
                if (pecaPreta.getIdentificador().equals(movimentoParaPeca)) {
                    pecaPreta.estadoPecaCapturado();
                    pecaPreta.x = "";
                    pecaPreta.y = "";
                    blackTeam.remove(pecaPreta);
                    pecaCapturada = true;
                    statusBranca.incCaptures();
                    break;
                }
            }
            statusBranca.incValidMoves();
        }

        if (pecaCapturada) {
            tabuleiro.algumaPecaMorreu();
        }

        if (tabuleiro.getPecaMorta() && !pecaCapturada) {
            gameResults.incJogadasSemCaptura();
        }

        cordenadasPecasArray[y0][x0] = null;
        cordenadasPecasArray[y1][x1] = pecaAtual;

        for (Peca pecaTemporaria : pecas) {
            if (pecaTemporaria.getIdentificador().equals(pecaAtual)) {
                pecaTemporaria.setX(Integer.toString(x1));
                pecaTemporaria.setY(Integer.toString(y1));
            }
        }

        tabuleiro.changeTurnInGame();

        return true;
    }


    public int getCurrentTeamID() {
        return tabuleiro.isBlackTurn() ? 10 : 20;
    }


    public boolean gameOver() {
        if ((whiteTeam.size()==1 && blackTeam.size()==1) || (gameResults.getJogadasSemCaptura()>=10 && tabuleiro.getPecaMorta())) {
            gameResults.jogoEmpatado();
            return true;
        }

        if (whiteTeam.isEmpty()) {
            gameResults.pretasGanham();
            return true;
        }

        if (blackTeam.isEmpty()) {
            gameResults.brancasGanham();
            return true;
        }

        return false;
    }

    public ArrayList<String> getGameResults() {
        ArrayList<String> placar = new ArrayList<>();

        placar.add("JOGO DE CRAZY CHESS");
        placar.add("Resultado: " + gameResults.getResultadoJogo());
        placar.add("---");
        placar.add("Equipa das Pretas");
        placar.add(Integer.toString(statusPreta.getCaptures()));
        placar.add(Integer.toString(statusPreta.getValidMoves()));
        placar.add(Integer.toString(statusPreta.getInvalidMoves()));
        placar.add("Equipa das Brancas");
        placar.add(Integer.toString(statusBranca.getCaptures()));
        placar.add(Integer.toString(statusBranca.getValidMoves()));
        placar.add(Integer.toString(statusBranca.getInvalidMoves()));
        return placar;
    }

    void saveGame(File file) throws IOException {
        int x = 0;
        int y = 0;
        boolean achou;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(tabuleiro.getTamanhoTabuleiro() + "\n");
            writer.write(tabuleiro.getNumPecaTotal() + "");

            for (int i = 0; i < tabuleiro.getNumPecaTotal(); i++) {
                writer.newLine();
                writer.write(pecas.get(i).getIdentificador() + ":");
                writer.write( pecas.get(i).getTipoDePeca() + ":");
                writer.write( pecas.get(i).getEquipa() + ":");
                writer.write(pecas.get(i).getAlcunha());
            }

            while (y < tabuleiro.getTamanhoTabuleiro()) {
                writer.newLine();

                while (x < tabuleiro.getTamanhoTabuleiro()) {
                    achou = false;

                    for (int idPeca = 0; idPeca < tabuleiro.getNumPecaTotal(); idPeca++) {
                        if (!pecas.get(idPeca).getEstado().equals("em jogo")) {
                            continue;
                        }

                        if (pecas.get(idPeca).getY().equals(y + "")) {
                            if (pecas.get(idPeca).getX().equals(x + "")) {
                                writer.write(pecas.get(idPeca).getIdentificador());
                                x++;

                                if (x < tabuleiro.getTamanhoTabuleiro()) {
                                    writer.write(":");
                                }
                                achou = true;
                                break;
                            }
                        }
                    }

                    if (!achou) {
                        writer.write("0");
                        x++;

                        if (x < tabuleiro.getTamanhoTabuleiro()) {
                            writer.write(":");
                        }
                    }
                }
                y++;
                x = 0;
            }
        }
    }

    void undo() {
    }

    List<Comparable> getHints(int x, int y) {
        return null;
    }

    public JPanel getAuthorsPanel() {
        return null;
    }
}