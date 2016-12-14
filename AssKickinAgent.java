package info.kwarc.teaching.AI.Kalah.WS1617.agents;
import scala.Tuple4;
import info.kwarc.teaching.AI.Kalah.*;
import java.util.ArrayList;

public class AssKickinAgent extends Agent{
	private int we;
	
	private int[][] _houses;
	private int[] _stores;
	private Board _board;
	/*
	 * Takes state of the game, which player's turn it is and which move to do
	 * houses are modified in situ, store of the current player is returned
	 * Note: each players houses "count upwards to their own store"
	 */
	public static int nextState(int[][] board, int[] stores, int player, int move){

		int toSpend = board[player][move];
		int playerSide = player;
		int curIndex = move;
		if(board[player][move] == 0)
			return -1;
		board[player][move] =0;

		while(toSpend > 0){
			if(++curIndex == board[player].length){
				if(playerSide == player){
					++stores[player];
					--toSpend;
				}
				playerSide = (playerSide +1)%2;
				curIndex = -1;//-1 so increment gets us to the (correct) 0
				continue;
			}
			++board[playerSide][curIndex];
			toSpend--;
		}
		return 0;
	}
	
	public int[] toIntArray(ArrayList<Integer> list){
		int[] ret = new int[list.size()];
		for(int i=0; i<list.size(); i++){
			ret[i] = list.get(i).intValue();
		}
		return ret;
 	}

	private void refreshBoard(Board board){
		boolean playerOne = we==0;
		_houses[0] = toIntArray(playerOne?getMyHouses(board, playerOne):getEnemyHouses(board, playerOne));
		_houses[1] = toIntArray(playerOne?getEnemyHouses(board, playerOne):getMyHouses(board, playerOne));

		_stores[0] = playerOne?getMyStoreSeeds(board, playerOne):getEnemyStoreSeeds(board, playerOne);
		_stores[1] = playerOne?getEnemyStoreSeeds(board, playerOne):getMyStoreSeeds(board, playerOne);

	}
	@Override
	public void init(Board board, boolean playerOne) {
		we = playerOne?0:1;
		System.out.println(we + " " + playerOne);
		_stores = new int[2];
		_houses = new int[2][board.houses()];
		_board = board;
		refreshBoard(_board);
	}

	private static String fixedLengthString(String string, int length) {
		return String.format("%1$"+length+ "s", string);
	}

	private static String fixedLengthInt(int integer, int length) {
		return fixedLengthString(Integer.toString(integer), length);
	}
	public static void printGameState(int[] housesOne, int[] housesTwo, int storeOne, int storeTwo){
		for(int i = 0; i<housesOne.length; ++i){
			System.out.print(fixedLengthInt(housesTwo[i], 4));
		}
		System.out.println();
		System.out.print(storeOne);
		System.out.println(fixedLengthInt(storeTwo, housesTwo.length*4+3));

		for(int i = housesTwo.length-1; i >= 0; --i){
			System.out.print(fixedLengthInt(housesOne[i], 4));
		}
		System.out.println();
	}
	
	private double heuristic(int[][] h, int[] s){
		int ourseeds = s[we];
		int ourhouses = 0;
		int enemy = (we+1)%2;
		int enemyhouses = 0;
		int dangerdanger = 0;

		for(int i = 0; i < h[we].length; i++){
			ourhouses += h[we][i];
			enemyhouses += h[enemy][i];
			if(h[enemy][i] == 0)
				dangerdanger += h[we][i];
		}
		
		return 5*ourseeds + ourhouses - enemyhouses - 0.5*dangerdanger;
	}
    
	@Override
	public int move() {
		System.out.println("=== PLAYER " + we + " ===");
		refreshBoard(_board);
		int best_move = -1;
		double best_value = -10000;
		for(int i = 0; i < _houses[we].length; i++){
			
			int[][] h = new int[2][_houses[0].length];
			int[] s = new int[2];
			for (int j =0; j < _houses[0].length; j++){
				h[0][j] = _houses[0][j];
				h[1][j] = _houses[1][j];
			}
			s[0] = _stores[0];
			s[1] = _stores[1];
			
			//int[][] h = new int[2][_houses[0].length];
			//int[] s = new int[2];
			//System.arraycopy(_houses, 0, h, 0, length);
			int ret = nextState(h, s, we, i);
			if(ret==0){
				double val = heuristic(h,s);
				printGameState(h[0],h[1],s[0],s[1]);
				System.out.println("Move: " + i + ", " + val);
				if(val > best_value){
					best_value = val;
					best_move = i;
				}
			}
		}
		return best_move+1;
	}

	@Override
	public String name() {
		return "AssKickinAgent";
	}

	@Override
	public Iterable<String> students() {
		ArrayList<String> students = new ArrayList<String>();
		students.add("Magnus Berendes");
		students.add("Sebastian Rietsch");
		return students;
	}

    static ArrayList<Integer> getMyHouses(Tuple4<Iterable<Object>, Iterable<Object>, Object, Object> obj, boolean playerOne)
    {
        Iterable<Object> houses;
        if(playerOne==true)
        {
            houses = obj._1();
        }
        else
        {
            houses = obj._2();
        }

        ArrayList<Integer> housesJava=new ArrayList<>();
        houses.forEach(item->housesJava.add((Integer)item));

        return housesJava;
    }

    /**
     * Returns a Java-ArrayList containing enemy houses and their current seed number
     * @return The list of numbers of seeds in the houses of enemy
     */
    static ArrayList<Integer> getEnemyHouses(Tuple4<Iterable<Object>, Iterable<Object>, Object, Object> obj, boolean playerOne)
    {
        Iterable<Object> houses;
        if(playerOne==true)
        {
            houses = obj._2();
        }
        else
        {
            houses = obj._1();
        }

        ArrayList<Integer> housesJava=new ArrayList<>();
        houses.forEach(item->housesJava.add((Integer)item));

        return housesJava;
    }

    /**
     * Returns a Java-Integer representing the seeds in your store
     * @return The list of numbers of seeds in the your store
     */
    static Integer getMyStoreSeeds(Tuple4<Iterable<Object>, Iterable<Object>, Object, Object> obj, boolean playerOne)
    {
        if(playerOne==true)
        {
            return (Integer) obj._3();
        }
        return (Integer) obj._4();
    }

    /**
     * Returns a Java-Integer representing the seeds in enemy store
     * @return The list of numbers of seeds in the enemy store
     */
    static Integer getEnemyStoreSeeds(Tuple4<Iterable<Object>, Iterable<Object>, Object, Object> obj, boolean playerOne)
    {
        if(playerOne==true)
        {
            return (Integer) obj._4();
        }
        return (Integer) obj._3();
    }

    /**
     * Returns a Java-ArrayList containing your houses and their current seed number
     * @return The list of numbers of seeds in the houses of you
     */
    static ArrayList<Integer> getMyHouses(Board board, boolean playerOne)
    {
        return getMyHouses(board.getState(),playerOne);
    }

    /**
     * Returns a Java-ArrayList containing enemy houses and their current seed number
     * @return The list of numbers of seeds in the houses of enemy
     */
    static ArrayList<Integer> getEnemyHouses(Board board, boolean playerOne)
    {
        return getEnemyHouses(board.getState(),playerOne);
    }

    /**
     * Returns a Java-Integer representing the seeds in your store
     * @return The list of numbers of seeds in the your store
     */
    static Integer getMyStoreSeeds(Board board, boolean playerOne)
    {
        return getMyStoreSeeds(board.getState(),playerOne);
    }

    /**
     * Returns a Java-Integer representing the seeds in enemy store
     * @return The list of numbers of seeds in the enemy store
     */
    static Integer getEnemyStoreSeeds(Board board, boolean playerOne)
    {
        return getEnemyStoreSeeds(board.getState(),playerOne);
    }
}
