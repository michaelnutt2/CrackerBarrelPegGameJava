
import java.io.*;
import java.util.*;

public class CrackerBarrelGame{
  public static void main(String[] args){
    int k = 1;
    for(int i = 0; i < 5; i++)
    {
      for(int j = 0; j <= i; j++)
      {
        System.out.println("=== " + k + " ===");
        k++;
        Play play = new Play(i, j);
        play.Solve();
      }
    }
  }

  static class Board{
    boolean[][] pegs = new boolean[5][5];

    public Board(int row, int col){
      for(int i = 0; i < 5; i++)
      {
        for(int j = 0; j <= i; j++)
        {
          pegs[i][j] = true;
        }
      }
      pegs[row][col] = false;
    }

    public Board(int board){
      for(int i = 4; i >= 0; i--)
      {
        for(int j = i; j >= 0; j--)
        {
          if((board & 1) == 1)
            pegs[i][j] = true;
          else
            pegs[i][j] = false;
          board /= 2;
        }
      }
    }

    public Board(Board old) {
      for(int i = 0; i < 5; i++)
      {
        for(int j = 0; j <= i; j++)
        {
          pegs[i][j] = old.pegs[i][j];
        }
      }
    }

    public List<Board> potentialBoards(){
      List<Board> boards = new ArrayList<Board>();

      for(int i = 0; i < 5; i++)
      {
        for(int j = 0; j <= i; j++)
        {
          Slot start = new Slot(i, j);
          List<Move> potentialMoves = Moves.getMoves(start);
          for(Move move : potentialMoves) {
            if(validMove(move))
              boards.add(jump(move));
          }
        }
      }
      return boards;
    }

    public boolean validMove(Move move) {
      if(!pegs[move.getStart().getRow()][move.getStart().getCol()])
        return false;
      if(!pegs[move.getJump().getRow()][move.getJump().getCol()])
        return false;
      if(pegs[move.getEnd().getRow()][move.getEnd().getCol()])
        return false;

      return true;
    }

    public Board jump(Move move){
      Board bd = new Board(this);

      bd.pegs[move.getStart().getRow()][move.getStart().getCol()] = false;
      bd.pegs[move.getJump().getRow()][move.getJump().getCol()] = false;
      bd.pegs[move.getEnd().getRow()][move.getEnd().getCol()] = true;

      return bd;
    }

    public boolean solvedBoard() {
      int numPegsRemain = 0;

      for(int i = 0; i < 5; i++){
        for(int j = 0; j <= i; j++)
        {
          if(pegs[i][j]){
            numPegsRemain++;
            if(numPegsRemain > 1)
              return false;
          }
        }
      }
      return numPegsRemain == 1;
    }

    public int toInt() {
      int ret = 0;
      for(int i = 0; i < 5; ++i)
  			for (int j = 0; j <= i; ++j) {
  				ret *= 2;
  				if (pegs[i][j]) {
  					ret |= 1;
  				}
  			}

  		return ret;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < 5; ++i) {
        for (int s = 4-i; s > 0; --s)
          sb.append(" ");
        for (int j = 0; j <= i; ++j) {
          sb.append(pegs[i][j] ? 'x' : '.').append(" ");
        }
        sb.append("\n");
      }
      return sb.toString();
    }
  }

  static class Play{
    Board startingBoard;

    public Play(int row, int col){
      startingBoard = new Board(row, col);
    }

    public void Solve(){
      Sequence startingSlot = new Sequence(startingBoard);

      for(Board nextBoard : startingBoard.potentialBoards()){
        Sequence nextNode = new Sequence(nextBoard);
        if(play(nextBoard, nextNode))
          startingSlot.addChild(nextNode);
      }
      printSolution(startingSlot);
    }

    private void printSolution(Sequence parent){
      System.out.println(parent.getBoard());

      if(parent.numChildren() > 0) {
  			Sequence nextNode = parent.getFirstChild();
  			printSolution(nextNode);				// recurse
  			if (nextNode.numChildren() == 0)
  				parent.removeFirstChild();
        }
    }

    private boolean play(Board bd, Sequence parent){
      if(bd.solvedBoard())
        return true;

      List<Board> nextBoards = bd.potentialBoards();

      boolean found = false;

      for(Board nextBoard : nextBoards) {
        Sequence nextNode = new Sequence(nextBoard);
        if(play(nextBoard, nextNode)){
          found = true;
          parent.addChild(nextNode);
        }
      }

      return found;
    }
  }

  static class Sequence {
    Sequence level;
    Board bd;
    List<Sequence> children = new ArrayList<Sequence>();

    public Sequence(Board bd){
      this.bd = bd;
    }

    public void addChild(Sequence child){
      children.add(child);
    }

    public Board getBoard(){
      return bd;
    }

    public boolean hasChildren(){
      return children.size() > 0;
    }

    public Sequence getFirstChild() {
      return children.get(0);
    }

    public void removeFirstChild(){
      children.remove(0);
    }

    public int numChildren() {
      return children.size();
    }
  }

  static class Slot{
    int row;
    int col;

    public Slot(int row, int col){
      this.row = row;
      this.col = col;
    }

    public int getRow(){
      return row;
    }

    public int getCol(){
      return col;
    }

    public String toString(){
      return "[" + row + "," + col + "]";
    }

    public int hashCode(){
      int result = 17;
      result = 37*result+row;
      result = 37*result+col;

      return result;
    }

    public boolean equals(Object other){
      if(!(other instanceof Slot))
        return false;

      Slot that = (Slot) other;

      if(this.row != that.row)
        return false;

      return this.col == that.col;
    }
  }

  static class Move{
    private Slot start;
    private Slot jump;
    private Slot end;

    public Move(Slot start, Slot jump, Slot end){
      this.start = start;
      this.jump = jump;
      this.end = end;
    }

    public Slot getStart() {
      return start;
    }

    public Slot getJump() {
      return jump;
    }

    public Slot getEnd() {
      return end;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();

      sb.append("{"+start);
      sb.append(","+jump);
      sb.append(","+end+ "}");

      return sb.toString();
    }
  }

  static class Moves {
  	private static Map<Slot,List<Move>> validMoves = new HashMap<Slot,List<Move>>();

  	static {
  		/*
  		 *          0,0
  		 *       1,0  1,1
  		 *     2,0  2,1  2,2
  		 *   3,0  3,1  3,2  3,3
  		 * 4,0  4,1  4,2  4,3  4,4
  		 *
  		 */
  		Slot start;

  		start = new Slot(0,0);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(1,0), new Slot(2,0)));
  		validMoves.get(start).add(new Move(start, new Slot(1,1), new Slot(2,2)));

  		start = new Slot(1,0);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(2,0), new Slot(3,0)));
  		validMoves.get(start).add(new Move(start, new Slot(2,1), new Slot(3,2)));

  		start = new Slot(1,1);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(2,1), new Slot(3,1)));
  		validMoves.get(start).add(new Move(start, new Slot(2,2), new Slot(3,3)));

  		start = new Slot(2,0);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(1,0), new Slot(0,0)));
  		validMoves.get(start).add(new Move(start, new Slot(2,1), new Slot(2,2)));
  		validMoves.get(start).add(new Move(start, new Slot(3,0), new Slot(4,0)));
  		validMoves.get(start).add(new Move(start, new Slot(3,1), new Slot(4,2)));

  		start = new Slot(2,1);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(3,1), new Slot(4,1)));
  		validMoves.get(start).add(new Move(start, new Slot(3,2), new Slot(4,3)));

  		start = new Slot(2,2);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(1,1), new Slot(0,0)));
  		validMoves.get(start).add(new Move(start, new Slot(2,1), new Slot(2,0)));
  		validMoves.get(start).add(new Move(start, new Slot(3,2), new Slot(4,2)));
  		validMoves.get(start).add(new Move(start, new Slot(3,3), new Slot(4,4)));

  		start = new Slot(3,0);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(2,0), new Slot(1,0)));
  		validMoves.get(start).add(new Move(start, new Slot(3,1), new Slot(3,2)));

  		start = new Slot(3,1);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(2,1), new Slot(1,1)));
  		validMoves.get(start).add(new Move(start, new Slot(3,2), new Slot(3,3)));

  		start = new Slot(3,2);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(2,1), new Slot(1,0)));
  		validMoves.get(start).add(new Move(start, new Slot(3,1), new Slot(3,0)));

  		start = new Slot(3,3);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(2,2), new Slot(1,1)));
  		validMoves.get(start).add(new Move(start, new Slot(3,2), new Slot(3,1)));

  		start = new Slot(4,0);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(3,0), new Slot(2,0)));
  		validMoves.get(start).add(new Move(start, new Slot(4,1), new Slot(4,2)));

  		start = new Slot(4,1);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(3,1), new Slot(2,1)));
  		validMoves.get(start).add(new Move(start, new Slot(4,2), new Slot(4,3)));

  		start = new Slot(4,2);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(3,1), new Slot(2,0)));
  		validMoves.get(start).add(new Move(start, new Slot(3,2), new Slot(2,2)));
  		validMoves.get(start).add(new Move(start, new Slot(4,1), new Slot(4,0)));
  		validMoves.get(start).add(new Move(start, new Slot(4,3), new Slot(4,4)));

  		start = new Slot(4,3);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(3,2), new Slot(2,1)));
  		validMoves.get(start).add(new Move(start, new Slot(4,2), new Slot(4,1)));

  		start = new Slot(4,4);
  		validMoves.put(start, new ArrayList<Move>());
  		validMoves.get(start).add(new Move(start, new Slot(3,3), new Slot(2,2)));
  		validMoves.get(start).add(new Move(start, new Slot(4,3), new Slot(4,2)));
  	}


  	public static List<Move> getMoves(Slot position) {
  		if (!validMoves.containsKey(position))
  			throw new RuntimeException("Invalid position: " + position);

  		return validMoves.get(position);
  	}


  	public String toString() {
  		return validMoves.toString();
  	}
  }

}
