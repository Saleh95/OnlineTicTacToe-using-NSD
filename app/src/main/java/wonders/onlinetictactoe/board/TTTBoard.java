package wonders.onlinetictactoe.board;


public class TTTBoard {
    private char board[];
    public static int size = 9;
    public static char Empty = ' ';
    public static char P1 = '+';
    public static char P2 = '-';
    private int player; // 1: player 1; 2: player 2T;
    private static TTTBoard TTT;

    private TTTBoard() {
        board = new char[size];
        for (int i=0; i<size; ++i)
            board[i] = Empty;
        player = 1;
    }

    public static TTTBoard getInstance(){
        if(TTT==null)
            TTT = new TTTBoard();
        return TTT;
    }

    public int getPlayer() {
        return player;
    }

    public void set(int pos){

        if (player==1 && board[pos]==Empty) board[pos] = P1;
        if (player==2 && board[pos]==Empty) board[pos] = P2;
        player = player%2+1;
    }

    public void reset(){
        for (int i=0; i<size; ++i)
            board[i] = Empty;
        player = 1;
    }

    public int Checker(){
        int j=0;
        //check cross
        if (board[0]!=Empty&&board[0]==board[4]&&board[0]==board[8])
            return (board[j]==P1)?1:2;

        if (board[2]!=Empty&&board[2]==board[4]&&board[2]==board[6])
            return (board[j]==P1)?1:2;
        //check rows and cols
        while (j<9){
            if (board[j]!=Empty&&board[j]==board[j+1]&&board[j]==board[j+2])
                return (board[j]==P1)?1:2;

            if(j<3){
                if (board[j]!=Empty&&board[j]==board[j+3]&&board[j]==board[j+6])
                    return (board[j]==P1)?1:2;}
            j+=3;
        }

        return -1;
    }

    public char get(int pos){
        return board[pos];
    }
}
