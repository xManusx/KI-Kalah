import java.util.*;

public class KalahUtil {


	public static void main(String[] args){
		final int INIT_SEEDS = 4;
		final int NO_OF_HOUSES = 5;

		System.out.println("==KALAH("+INIT_SEEDS+","+NO_OF_HOUSES+")==");
		int[][] board = new int[2][NO_OF_HOUSES];
		int[] stores = new int[2];
		for(int i = 0; i< NO_OF_HOUSES; ++i){
			board[0][i] = INIT_SEEDS;
			board[1][i] = INIT_SEEDS;
		}

		KalahUtil.printGameState(board[0], board[1], stores[0], stores[1]);
		int player = 1;
		stores[player] = KalahUtil.nextState(board[0], board[1], stores[0], stores[1], player==0, 2);
		KalahUtil.printGameState(board[0], board[1], stores[0], stores[1]);

	}

		/*
		 * Finds the house with the least "spillover"
		 * into enemy houses, if there are more than one
		 * returned value is undefined (but a legal move)
		 *
		 * Doesn't take very full houses into account
		 * where the spillover goes back into our side
		 */
	public static int findLeastSpillover(int[][] board, int player){
		int numberOfHouses = board[player].length;
		int minimum = Integer.MAX_VALUE;
		int index = -1;
		for(int i = 0; i<numberOfHouses; ++i){
			if(board[player][i] == 0) continue;
			if(board[player][i] - (numberOfHouses -i) < minimum){
				minimum = board[player][i] - (numberOfHouses -i);
				index = i;
			}
		}
		return index;
	}

	/*
	 * Returns a random legal move for player
	 */
	public static int returnRandomLegalMove(int[][] board, int player){
		Random rand = new Random();
		int index;
		do{
			index = rand.nextInt(board[player].length);
		} while(board[player][index] == 0);
		return index;
	}

	/*
	 * Checks if there is a move resulting
	 * in gaining another turn for player
	 */
	public static int findIfAgainTurn(int[][] board, int player){
		int numberOfHouses = board[player].length;
		for(int i = 0; i<numberOfHouses; ++i){
			if(board[player][i] == numberOfHouses - i){
				return i;
			}
		}
		return -1;
	}

	/*
	 * Takes state of the game, which player's turn it is and which move to do
	 * houses are modified in situ, store of the current player is returned
	 * Note: each players houses "count upwards to their own store"
	 */
	public static int nextState(int[] housesOne, int[] housesTwo, int storeOne, int storeTwo, boolean playerOnesTurn, int move){
		int[][] board = {housesOne, housesTwo};
		int[] stores = {storeOne, storeTwo};
		int player = (playerOnesTurn)?0:1;

		int toSpend = board[player][move];
		int playerSide = player;
		int curIndex = move;
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
		return stores[player];
	}

/*
 * Stuff for printing
 */
private static String fixedLengthString(String string, int length) {
	return String.format("%1$"+length+ "s", string);
}

private static String fixedLengthInt(int integer, int length) {
	return fixedLengthString(Integer.toString(integer), length);
}


/*
 * Player One is shown at the bottom,
 * their houses are counted clockwise, "from the right"
 */
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
}
