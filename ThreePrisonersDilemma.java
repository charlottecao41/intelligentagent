public class ThreePrisonersDilemma {
	
	/* 
	 This Java program models the two-player Prisoner's Dilemma game.
	 We use the integer "0" to represent cooperation, and "1" to represent 
	 defection. 
	 
	 Recall that in the 2-players dilemma, U(DC) > U(CC) > U(DD) > U(CD), where
	 we give the payoff for the first player in the list. We want the three-player game 
	 to resemble the 2-player game whenever one player's response is fixed, and we
	 also want symmetry, so U(CCD) = U(CDC) etc. This gives the unique ordering
	 
	 U(DCC) > U(CCC) > U(DDC) > U(CDC) > U(DDD) > U(CDD)
	 
	 The payoffs for player 1 are given by the following matrix: */
	
	static int[][][] payoff = {  
		{{6,3},  //payoffs when first and second players cooperate 
		 {3,0}}, //payoffs when first player coops, second defects
		{{8,5},  //payoffs when first player defects, second coops
	     {5,2}}};//payoffs when first and second players defect
	
	/* 
	 So payoff[i][j][k] represents the payoff to player 1 when the first
	 player's action is i, the second player's action is j, and the
	 third player's action is k.
	 
	 In this simulation, triples of players will play each other repeatedly in a
	 'match'. A match consists of about 100 rounds, and your score from that match
	 is the average of the payoffs from each round of that match. For each round, your
	 strategy is given a list of the previous plays (so you can remember what your 
	 opponent did) and must compute the next action.  */
	
	
	abstract class Player {
		// This procedure takes in the number of rounds elapsed so far (n), and 
		// the previous plays in the match, and returns the appropriate action.
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			throw new RuntimeException("You need to override the selectAction method.");
		}
		
		// Used to extract the name of this player class.
		final String name() {
			String result = getClass().getName();
			return result.substring(result.indexOf('$')+1);
		}
	}
	
	/* Here are four simple strategies: */
	
	class NicePlayer extends Player {
		//NicePlayer always cooperates
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return 0; 
		}
	}
	
	class NastyPlayer extends Player {
		//NastyPlayer always defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return 1; 
		}
	}
	
	class RandomPlayer extends Player {
		//RandomPlayer randomly picks his action each time
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (Math.random() < 0.5)
				return 0;  //cooperates half the time
			else
				return 1;  //defects half the time
		}
	}
	class ImprovedTicForTacPlayer extends Player {
		//tic for tac but not very tolerant
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			if ((oppHistory1[n-1]==0) && (oppHistory2[n-1]==0))
				return 0;
			else
				return 1;

		}
	}
	
	class TolerantPlayer extends Player {
		//TolerantPlayer looks at his opponents' histories, and only defects
		//if at least half of the other players' actions have been defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			int opponentDefect = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			if (opponentDefect > opponentCoop)
				return 1;
			else
				return 0;
		}
	}

	class StochasticPlayer extends Player {
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			float opp1coopchance = 0;
			float opp2coopchance = 0;
			float opp1vengeance = 0;
			float opp2vengeance = 0;
			float opp1nice = 0;
			float opp2nice = 0;
			int myDefect = 0;
			int myCoop = 0;
			if (n==0)
				return 0;

			if (n >= 109)
            	return 1; // opponents cannot retaliate

			// play along if (c,c,c) and (d,d,d) is reached- opponents do not want to move away.
			if (oppHistory1[n-1] == oppHistory2[n-1])
            	return oppHistory1[n-1];

			//calculate cooperation chance of the opponent
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
			}

			opp1coopchance = opponentCoop/n;

			opponentCoop = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
			}

			opp2coopchance = opponentCoop/n;

			float cooppayoff = opp1coopchance*opp2coopchance*(float)(payoff[0][0][0])+(1-opp1coopchance)*opp2coopchance*(float)(payoff[0][1][0])+opp1coopchance*(1-opp2coopchance)*(float)(payoff[0][0][1])+(1-opp1coopchance)*(1-opp2coopchance)*(float)(payoff[0][1][1]);
			float defectpayoff = opp1coopchance*opp2coopchance*(float)(payoff[1][0][0])+(1-opp1coopchance)*opp2coopchance*(float)(payoff[1][1][0])+opp1coopchance*(1-opp2coopchance)*(float)(payoff[1][0][1])+(1-opp1coopchance)*(1-opp2coopchance)*(float)(payoff[1][1][1]);

			//calculate the vengeance and niceness score of the opponent
			 for (int i = 0; i < n-1; ++i) {
					myDefect = myDefect + 1;
					if  (myHistory[i]== 1)
					{
						if (oppHistory1[i+1]==1)
							opp1vengeance= opp1vengeance+1;

						if (oppHistory2[i+1]==1)
							opp2vengeance= opp2vengeance+1;
					};

					if  (myHistory[i]== 0)
					{
						if (oppHistory1[i+1]==1)
							opp1vengeance= opp1vengeance+1;

						if (oppHistory2[i+1]==1)
							opp2vengeance= opp2vengeance+1;
					};
				}

			float vengeanceidx1 = opp1vengeance/myDefect;
			float vengeanceidx2 = opp2vengeance/myDefect;
			float nicep1 = opp1nice/myCoop;
			float nicep2 = opp2nice/myCoop;
			
			//perform one step look ahead
			defectpayoff = defectpayoff+ (1-vengeanceidx2)*(1-vengeanceidx1)*opp1coopchance*opp2coopchance*(float)(payoff[1][0][0])
				+(1-vengeanceidx2)*vengeanceidx1*(1-opp1coopchance)*opp2coopchance*(float)(payoff[1][1][0])
				+vengeanceidx2*(1-vengeanceidx1)*opp1coopchance*(1-opp2coopchance)*(float)(payoff[1][0][1])
				+vengeanceidx2*(vengeanceidx1)*(1-opp1coopchance)*(1-opp2coopchance)*(float)(payoff[1][1][1]);

				
			cooppayoff = cooppayoff
				+nicep1*nicep2*opp1coopchance*opp2coopchance*(float)(payoff[0][0][0])
				+(1-nicep1)*nicep2*(1-opp1coopchance)*opp2coopchance*(float)(payoff[0][1][0])
				+nicep1*(1-nicep2)*opp1coopchance*(1-opp2coopchance)*(float)(payoff[0][0][1])
				+(1-nicep1)*(1-nicep2)*(1-opp1coopchance)*(1-opp2coopchance)*(float)(payoff[0][1][1]);

			
			if (defectpayoff > cooppayoff)
				return 1;
			else
				return 0;
			}
	}

	class UtilityPlayer extends Player {
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			float opp1coopchance = 0;
			float opp2coopchance = 0;
			if (n==0)
				return 0;
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
			}

			opp1coopchance = opponentCoop/n;

			opponentCoop = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
			}

			opp2coopchance = opponentCoop/n;

			float cooppayoff = opp1coopchance*opp2coopchance*(float)(payoff[0][0][0])+(1-opp1coopchance)*opp2coopchance*(float)(payoff[0][1][0])+opp1coopchance*(1-opp2coopchance)*(float)(payoff[0][0][1])+(1-opp1coopchance)*(1-opp2coopchance)*(float)(payoff[0][1][1]);
			float deflectpayoff = opp1coopchance*opp2coopchance*(float)(payoff[1][0][0])+(1-opp1coopchance)*opp2coopchance*(float)(payoff[1][1][0])+opp1coopchance*(1-opp2coopchance)*(float)(payoff[1][0][1])+(1-opp1coopchance)*(1-opp2coopchance)*(float)(payoff[1][1][1]);
			if (deflectpayoff > cooppayoff)
				return 1;
			else
				return 0;
		}
	}



	

	class TolerantPlayer60 extends Player {
		//TolerantPlayer looks at his opponents' histories, and only defects
		//if at least half of the other players' actions have been defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			int opponentDefect = 0;
			int threshold = (int)((n-1)*(0.6*2));
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			
			if (n<10)
				{if (opponentCoop>opponentDefect)
					return 0;
				else
					return 1;
				}
			else
			{
				if (opponentCoop>=threshold)
					return 0;
				else
					return 1;
			}
		}
	}
	class HybridPlayer extends Player { // extends Player
    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
		int opp1isnasty=0;
		int opp2isnasty=0;
		int myDefect = 0;
        int oppDefect1 = 0;
        int oppDefect2 = 0;
		int opponentCoop = 0;
        int opponentDefect = 0;


        if (n == 0)
            return 0; // cooperate by default, quickly reaches (c,c,c) equilibrium with likeminded agents and reap benefits.

        if (n >= 109)
            return 1; // opponents cannot retaliate

        // play along if (c,c,c) and (d,d,d) is reached- opponents do not want to move away.
        if (oppHistory1[n-1] == oppHistory2[n-1])
            return oppHistory1[n-1];

		if (n%2==0){
        for (int i = 0; i < n; i++) {
                if (oppHistory1[i] == 0)
                    opponentCoop += 1;
                else
                    opponentDefect += 1;

                if (oppHistory2[i] == 0)
                    opponentCoop += 1;
                else
                    opponentDefect += 1;
            }

            if (opponentDefect > opponentCoop)
				return 1;
			else
				if (opponentDefect <= opponentCoop)
					return 0;

			if (opponentCoop<opponentDefect)
				opp2isnasty = 1;

			if ((opp1isnasty==1) || (opp2isnasty==1)) 
				return 1;
			else return 0;

		}
        //in odd round 

        for (int i = 0; i < n; ++i) {
            myDefect += myHistory[i];
            oppDefect1 += oppHistory1[i];
            oppDefect2 += oppHistory2[i];
        }

        if (myDefect >= oppDefect1 && myDefect >= oppDefect2)
            return 1;
        else
            return 0;
    }
}

class HybridPlayer1 extends Player { // extends Player
    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
		int opp1isnasty=0;
		int opp2isnasty=0;
		int myDefect = 0;
        int oppDefect1 = 0;
        int oppDefect2 = 0;
		int opponentCoop = 0;
        int opponentDefect = 0;


        if (n == 0)
            return 0; // cooperate by default, quickly reaches (c,c,c) equilibrium with likeminded agents and reap benefits.

        if (n >= 109)
            return 1; // opponents cannot retaliate

        // play along if (c,c,c) and (d,d,d) is reached- opponents do not want to move away.
        if (oppHistory1[n-1] == oppHistory2[n-1])
            return oppHistory1[n-1];

		if (n%2==0){
        for (int i = 0; i < n; i++) {
                if (oppHistory1[i] == 0)
                    opponentCoop += 1;
                else
                    opponentDefect += 1;

                if (oppHistory2[i] == 0)
                    opponentCoop += 1;
                else
                    opponentDefect += 1;
            }

            if (opponentDefect > opponentCoop)
				return 1;
			else
				if (opponentDefect <= opponentCoop)
					return 0;

		}
        //in odd round 

        for (int i = 0; i < n; ++i) {
            myDefect += myHistory[i];
            oppDefect1 += oppHistory1[i];
            oppDefect2 += oppHistory2[i];
        }

        if (myDefect >= oppDefect1 && myDefect >= oppDefect2)
            return 1;
        else
            return 0;
    }
}

	class TolerantPlayer40 extends Player {
		//TolerantPlayer looks at his opponents' histories, and only defects
		//if at least half of the other players' actions have been defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			int opponentDefect = 0;
			int threshold = (int)((n-1)*(0.6*2));

			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}
			
			if (n<10)
				{if (opponentCoop>opponentDefect)
					return 0;
				else
					return 1;
				}
			else
			{
				if (opponentCoop>=threshold)
					return 0;
				else
					return 1;
			}
		}
	}

	class CautiousPlayer extends Player {
		//CuatiousPlayer looks at his opponents' histories, and only defects
		//if at least half of the other players' actions have been defects
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			int opponentCoop = 0;
			int opponentDefect = 0;
			int israndom1 = 0;
			int israndom2 = 0;
			int opp1isnasty = 0;
			int opp2isnasty = 0;
			int threshold = (int)(n*0.5);


			//begin with nice
			if (n==0)
				return 0;

			//if both begin with nasty, just be nasty
			//similarly, if both begin with nice, just be be nice
			//if one nasty and one nice, be nasty, because this will benefit the nasty player should both of us being nice, therefore both will become nasty to make everyone lose
			if (n==1)
			{
				if ((oppHistory1[0]==1) || (oppHistory2[0]==1)) return 1;
				else return 0;
			}
			for (int i=0; i<n; i++) {
				if (oppHistory1[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}

			if (opponentCoop<opponentDefect)
				opp1isnasty = 1;

			opponentCoop = 0;
			opponentDefect = 0;
			for (int i=0; i<n; i++) {
				if (oppHistory2[i] == 0)
					opponentCoop = opponentCoop + 1;
				else
					opponentDefect = opponentDefect + 1;
			}

			if (opponentCoop<opponentDefect)
				opp2isnasty = 1;

			if ((opp1isnasty==1) || (opp2isnasty==1)) return 1;
			else return 0;
			
			}
		}
	
	class FreakyPlayer extends Player {
		//FreakyPlayer determines, at the start of the match, 
		//either to always be nice or always be nasty. 
		//Note that this class has a non-trivial constructor.
		int action;
		FreakyPlayer() {
			if (Math.random() < 0.5)
				action = 0;  //cooperates half the time
			else
				action = 1;  //defects half the time
		}
		
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			return action;
		}	
	}

	class T4TPlayer extends Player {
		//Picks a random opponent at each play, 
		//and uses the 'tit-for-tat' strategy against them 
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			if (Math.random() < 0.5)
				return oppHistory1[n-1];
			else
				return oppHistory2[n-1];
		}	
	}

	class Joss extends Player {
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //cooperate by default
			else{
				if (n%5==0)
					return 1;
				else
				{
					if (Math.random() < 0.5)
						return oppHistory1[n-1];
					else
						return oppHistory2[n-1];

				}
			}
		}	
	}

	class Tester extends Player {
		//Picks a random opponent at each play, 
		//and uses the 'tit-for-tat' strategy against them 
		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
			if (n==0) return 0; //defect by default
			else{

				if ((oppHistory1[n-1]==1 || oppHistory1[n-1]==1))
				// if either one retaliate, there is one T4T- so play T4T too.
					{
						if (Math.random() < 0.5)
							return oppHistory1[n-1];
						else
							return oppHistory2[n-1];
					}
				else
				//play random if there is no T4T
					{
						if (Math.random() < 0.5)
							return 1;
						else
							return 0;
					}
			}
		}	
	}



	
	/* In our tournament, each pair of strategies will play one match against each other. 
	 This procedure simulates a single match and returns the scores. */
	float[] scoresOfMatch(Player A, Player B, Player C, int rounds) {
		int[] HistoryA = new int[0], HistoryB = new int[0], HistoryC = new int[0];
		float ScoreA = 0, ScoreB = 0, ScoreC = 0;
		
		for (int i=0; i<rounds; i++) {
			int PlayA = A.selectAction(i, HistoryA, HistoryB, HistoryC);
			int PlayB = B.selectAction(i, HistoryB, HistoryC, HistoryA);
			int PlayC = C.selectAction(i, HistoryC, HistoryA, HistoryB);
			ScoreA = ScoreA + payoff[PlayA][PlayB][PlayC];
			ScoreB = ScoreB + payoff[PlayB][PlayC][PlayA];
			ScoreC = ScoreC + payoff[PlayC][PlayA][PlayB];
			HistoryA = extendIntArray(HistoryA, PlayA);
			HistoryB = extendIntArray(HistoryB, PlayB);
			HistoryC = extendIntArray(HistoryC, PlayC);
		}
		float[] result = {ScoreA/rounds, ScoreB/rounds, ScoreC/rounds};
		return result;
	}
	
//	This is a helper function needed by scoresOfMatch.
	int[] extendIntArray(int[] arr, int next) {
		int[] result = new int[arr.length+1];
		for (int i=0; i<arr.length; i++) {
			result[i] = arr[i];
		}
		result[result.length-1] = next;
		return result;
	}
	
	/* The procedure makePlayer is used to reset each of the Players 
	 (strategies) in between matches. When you add your own strategy,
	 you will need to add a new entry to makePlayer, and change numPlayers.*/
	
	int numPlayers = 15;
	Player makePlayer(int which) {
		switch (which) {
		case 0: return new NicePlayer();
		case 1: return new NastyPlayer();
		case 2: return new RandomPlayer();
		case 3: return new TolerantPlayer();
		case 4: return new FreakyPlayer();
		case 5: return new T4TPlayer();
		case 6: return new ImprovedTicForTacPlayer();
		case 7: return new CautiousPlayer();
		case 8: return new TolerantPlayer60();
		case 9: return new TolerantPlayer40();
		case 10: return new Tester();
		case 11: return new Joss();
		// case 12: return new HybridPlayer();
		case 12: return new StochasticPlayer();
		case 13: return new UtilityPlayer();
		case 14: return new HybridPlayer1();

		// case 0: return new NicePlayer();
		// case 1: return new NastyPlayer();
		// case 2: return new RandomPlayer();
		// case 3: return new TolerantPlayer();
		// case 4: return new FreakyPlayer();
		// case 5: return new T4TPlayer();
		// case 6: return new HybridPlayer();
		// case 7: return new HybridPlayer1();

		}
		throw new RuntimeException("Bad argument passed to makePlayer");
	}
	
	/* Finally, the remaining code actually runs the tournament. */
	
	public static void main (String[] args) {
		ThreePrisonersDilemma instance = new ThreePrisonersDilemma();
		instance.runTournament();
	}
	
	boolean verbose = true; // set verbose = false if you get too much text output
	
	void runTournament() {
		float[] totalScore = new float[numPlayers];

		// This loop plays each triple of players against each other.
		// Note that we include duplicates: two copies of your strategy will play once
		// against each other strategy, and three copies of your strategy will play once.

		for (int i=0; i<numPlayers; i++) for (int j=i; j<numPlayers; j++) for (int k=j; k<numPlayers; k++) {

				Player A = makePlayer(i); // Create a fresh copy of each player
				Player B = makePlayer(j);
				Player C = makePlayer(k);
				int rounds = 90 + (int)Math.rint(20 * Math.random()); // Between 90 and 110 rounds
				float[] matchResults = scoresOfMatch(A, B, C, rounds); // Run match
				totalScore[i] = totalScore[i] + matchResults[0];
				totalScore[j] = totalScore[j] + matchResults[1];
				totalScore[k] = totalScore[k] + matchResults[2];
				if (verbose)
					System.out.println(A.name() + " scored " + matchResults[0] +
							" points, " + B.name() + " scored " + matchResults[1] + 
							" points, and " + C.name() + " scored " + matchResults[2] + " points.");
		}
		int[] sortedOrder = new int[numPlayers];
		// This loop sorts the players by their score.
		for (int i=0; i<numPlayers; i++) {
			int j=i-1;
			for (; j>=0; j--) {
				if (totalScore[i] > totalScore[sortedOrder[j]]) 
					sortedOrder[j+1] = sortedOrder[j];
				else break;
			}
			sortedOrder[j+1] = i;
		}
		
		// Finally, print out the sorted results.
		if (verbose) System.out.println();
		System.out.println("Tournament Results");
		for (int i=0; i<numPlayers; i++) 
			System.out.println(makePlayer(sortedOrder[i]).name() + ": " 
				+ totalScore[sortedOrder[i]] + " points.");
		
	} // end of runTournament()
	
} // end of class PrisonersDilemma
