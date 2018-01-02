import java.util.*;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

enum RESULT { HIT, MISS, ALREADY_TAKEN,WIN, SUNK, ERROR};

// Store information about ship co-ordinates and hits
class Ship
{

	private int [] start;
	private int [] end;

	private int size;
	private int num_hit_pos;

	final private static int row_idx = 0;
	final private static int col_idx = 1;

	Ship(int [] start,int [] end)
	{
		this.start = start;
		this.end=end;

		if(start[row_idx] == end[row_idx])
			size = end[col_idx]-start[col_idx]+1;
		else
			size = end[row_idx]-start[row_idx]+1;

		num_hit_pos=0;
	}

	public boolean is_ship_sunken()
	{
		if(size == num_hit_pos)
			return true;
		else
			return false;
	}

	public void attack()
	{
		num_hit_pos++;
	}

}


// Main board of battleship
class Board
{
	final private static int ALREADY_HIT = -1;
	final private static int EMPTY_CELL = 0;
	final private static int row_idx = 0;
	final private static int col_idx = 1;
	final private static int NO_FLOATING_SHIPS=0;

	// shiindex to ship holding map
	private Map<Integer, Ship> floating_ships;
	// set of sunken ships
	private Set<Ship> sunken_ships;
	// board layout
	private int [][] layout;
	private int ship_idx;


	// for logging
	Logger LOGGER;

	private void init_logger(int lvl)
	{
		// logger initialization
		LOGGER = Logger.getLogger(Board.class.getName());
		Handler handlerObj =  new ConsoleHandler();
		if(lvl==0)
		{
			LOGGER.setLevel(Level.SEVERE);
			handlerObj.setLevel(Level.SEVERE);
		}
		else
		{
			LOGGER.setLevel(Level.ALL);
			handlerObj.setLevel(Level.ALL);
		}

		LOGGER.addHandler(handlerObj);
 		LOGGER.setUseParentHandlers(false);
	}

	Board(int brows, int bcols, int lvl)
	{
		layout = new int[brows][bcols];
		floating_ships = new HashMap<>();
		sunken_ships = new HashSet<>();
		ship_idx=1;

		init_logger(lvl);
	}

	private boolean checkIfShipFits(int [] start, int [] end)
	{
		// check if ship start and end position bit within layout
		// check if the cells are available
		// TBD-placeholder
		return true;
	}

	// mark board with ship-index to identify ship occoupying the cell
	private void mark_layout(int [] start, int [] end)
	{
		if(start[row_idx] == end[row_idx])
		{
			// mark horizontal
			for(int i=start[col_idx] ; i <= end[col_idx] ; i++)
			{
				layout[start[row_idx]][i] = ship_idx;
			}
		}
		else
		{
			// mark vertical
			for(int i=start[row_idx] ; i <= end[row_idx] ; i++)
				layout[i][start[col_idx]] = ship_idx;
		}
	}

	// add ship to the board
	public boolean add_ship(int [] start, int [] end)
	{
		if(checkIfShipFits(start,end)==false)
			return false;
		
		Ship sh = new Ship(start, end);
		// add to map of floating ships
		floating_ships.put(ship_idx,sh);
		// mark ship on the grid with ship index
		mark_layout(start,end);
		ship_idx++;
		return true;
	}

	private boolean  is_attack_valid(int row, int col)
	{
		// check if rows and cols are within range
		// TBD-place holder
		return true;
	}

	public RESULT attack_ship(int row, int col)
	{
		RESULT res;
		if(is_attack_valid(row,col) == false)
		{
			res = RESULT.ERROR;
			LOGGER.log(Level.FINE,"invalid attack");
		}
		else if(layout[row][col] == ALREADY_HIT)
		{
			res = RESULT.ALREADY_TAKEN;
			LOGGER.log(Level.FINE,"already taken");
		}
		else if(layout[row][col] == EMPTY_CELL)
		{
			layout[row][col] = ALREADY_HIT;
			res = RESULT.MISS;
			LOGGER.log(Level.FINE,"MISS");
		}
		else
		{
			// HITTING A SHIP
			int sh_idx=layout[row][col];
			Ship sh = floating_ships.get(sh_idx);
			if(sh == null)
			{
				LOGGER.log(Level.SEVERE,"Sorry: not able to find ship. critical program error");
				res=RESULT.ERROR;
			}
			else
			{
				sh.attack();
				layout[row][col] = ALREADY_HIT;
				if(sh.is_ship_sunken()==true)
				{
					floating_ships.remove(sh_idx);
					sunken_ships.add(sh);
					// if all floating ships taken down then return win
					if(floating_ships.size()==NO_FLOATING_SHIPS)
					{
						LOGGER.log(Level.FINE,"WIN " + row  + " " + col );
						res=RESULT.WIN;
					}
					else
					{
						LOGGER.log(Level.FINE,"HIT");
						res=RESULT.SUNK;
					}
				}
				else
				{
					LOGGER.log(Level.FINE,"HIT");
					res=RESULT.HIT;
				}
			}
			
		}
		return res;
	}
}


// Creating The game and Tests are run from the following class

class BattleShip
{
	static int TOTAL_TESTS = 5;
	int passed_tests;
	Board brd;

	BattleShip(int log)
	{
		brd = new Board(10,10,log);
		brd.add_ship(new int[] {0,0}, new int []{0,4});
	}
	// run tests
	public void run()
	{
		passed_tests=0;
		hit_ship_begin();
		hit_ship_end();
		hit_ship_again();
		hit_empty_spot();
		hit_and_check_win();
		System.out.println(passed_tests == TOTAL_TESTS ? "ALL Tests passed" : passed_tests + " tests passed out of total of " + TOTAL_TESTS);
	}

	// hit ship at start position
	private void hit_ship_begin()
	{
		if(brd.attack_ship(0,0)	== RESULT.HIT)
			passed_tests++;
		else
			System.out.println("hit_ship_begin test faled");
	}

	// hit ship at end position
	private void hit_ship_end()
	{
		if(brd.attack_ship(0,4)	== RESULT.HIT)
			passed_tests++;
		else
			System.out.println("hit_ship_end test faled");
	}

	// hit ship at already hit position
	private void hit_ship_again()
	{
		if(brd.attack_ship(0,0)	== RESULT.ALREADY_TAKEN)
			passed_tests++;
		else
			System.out.println("hit_ship_again test faled");
	}

	// hit an empty spot
	private void hit_empty_spot()
	{
		if(brd.attack_ship(0,5)	== RESULT.MISS)
			passed_tests++;
		else
			System.out.println("hit_empty_spot test faled");
	}

	// hit all ship positions  and check for win
	private void hit_and_check_win()
	{
		brd.attack_ship(0,1);
		brd.attack_ship(0,2);
		if(brd.attack_ship(0,3) == RESULT.WIN)
			passed_tests++;
		else
			System.out.println("hit_and_check_win test failed");
	}

	public static void main(String [] args)	
	{
		BattleShip test;
		if(args.length==1) 
		{
			// add logging
			test= new BattleShip(Integer.parseInt(args[0]));	
		}
		else
		{
			// no logging
			test = new BattleShip(0);
		}
		test.run();
	}
}
