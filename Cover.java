package pacman.controllers;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import static pacman.game.Constants.*;
public final class Cover extends Controller<EnumMap<GHOST,MOVE>>{
	public static final int CROWDED_DISTANCE=30;
	private final static float CONSISTENCY=0.9f;	//attack Ms Pac-Man with this probability
	private final static int PILL_PROXIMITY=15;		//power pill distance avoid
	public static final int PACMAN_DISTANCE=10;
	private final EnumMap<GHOST,Integer> cornerAllocation=new EnumMap<GHOST,Integer>(GHOST.class);
	Random rnd=new Random();
	EnumMap<GHOST,MOVE> myMoves=new EnumMap<GHOST,MOVE>(GHOST.class);
	
	public EnumMap<GHOST,MOVE> getMove(Game game,long timeDue)
	{
		for(GHOST ghost : GHOST.values())
		{	
			int currentIndex=game.getGhostCurrentNodeIndex(ghost);
			if(game.doesGhostRequireAction(ghost))
			{
				if(game.getGhostEdibleTime(ghost)>0 || closeToPower(game))	//retreat 
					if(isCrowded(game) && !closeToMsPacMan(game,currentIndex)){
						myMoves.put(ghost,getRetreatActions(game,ghost));
						}
					myMoves.put(ghost,game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(ghost),DM.PATH));
				else 
				{
					if(rnd.nextFloat()<CONSISTENCY)			//attack Ms Pac-Man otherwise (with certain probability)
						if(isCrowded(game) && !closeToMsPacMan(game,currentIndex)){
							myMoves.put(ghost,getRetreatActions(game,ghost));
						}
						else{
							myMoves.put(ghost,game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
									game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(ghost),DM.PATH));}
					else									//else rand legal move)
					{					
						MOVE[] possibleMoves=game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),game.getGhostLastMoveMade(ghost));
						if(isCrowded(game) && !closeToMsPacMan(game,currentIndex)){
							myMoves.put(ghost,getRetreatActions(game,ghost));
						}
						myMoves.put(ghost,possibleMoves[rnd.nextInt(possibleMoves.length)]);
					}
				}
			}
		}

		return myMoves;
	}
	
    //check if close to power pill
	private boolean closeToPower(Game game)
    {
    	int[] powerPills=game.getPowerPillIndices();
    	
    	for(int i=0;i<powerPills.length;i++)
    		if(game.isPowerPillStillAvailable(i) && game.getShortestPathDistance(powerPills[i],game.getPacmanCurrentNodeIndex())<PILL_PROXIMITY)
    			return true;

        return false;
    }
	 private boolean isCrowded(Game game)
	    {
	    	GHOST[] ghosts=GHOST.values();
	        float distance=0;
	        
	        for (int i=0;i<ghosts.length-1;i++)
	            for(int j=i+1;j<ghosts.length;j++)
	                distance+=game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]),game.getGhostCurrentNodeIndex(ghosts[j]));
	        
	        return (distance/6)<CROWDED_DISTANCE ? true : false;
	    }
	 private boolean closeToMsPacMan(Game game,int location)
	    {
	    	if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),location)<PACMAN_DISTANCE)
	    		return true;

	    	return false;
	    }
	 private MOVE getRetreatActions(Game game,GHOST ghost)
	    {
	    	int currentIndex=game.getGhostCurrentNodeIndex(ghost);
	    	int pacManIndex=game.getPacmanCurrentNodeIndex();
	    	
	        if(game.getGhostEdibleTime(ghost)==0 && game.getShortestPathDistance(currentIndex,pacManIndex)<PACMAN_DISTANCE)
	            return game.getApproximateNextMoveTowardsTarget(currentIndex,pacManIndex,game.getGhostLastMoveMade(ghost),DM.PATH);
	        else
	            return game.getApproximateNextMoveTowardsTarget(currentIndex,game.getPowerPillIndices()[cornerAllocation.get(ghost)],game.getGhostLastMoveMade(ghost),DM.PATH);
	    }
	}
}

