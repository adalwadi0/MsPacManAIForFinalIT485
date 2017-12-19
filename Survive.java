package pacman.controllers;

import static pacman.game.Constants.*;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.internal.Ghost;

public class Survive extends Controller<MOVE>{
	private static final int MIN_DISTANCE=10;	//if a ghost is this close, run away
	public MOVE getMove(Game game, long timeDue){
		int current=game.getPacmanCurrentNodeIndex();
		
		/*for(GHOST ghost : GHOST.values()){
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
		}*/
		int gnum = 0;
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
					gnum++;
			if(gnum>1)
				for(GHOST ghost2: GHOST.values()){
					int mdis = 10;
					if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost2))<mdis){
						mdis = game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
					}
					for(GHOST ghost3: GHOST.values()){
						if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost3))== mdis)
							return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
				}
				//return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
		}
			if(gnum==1){
				return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			}
		}
		
		
		int minDistance=Integer.MAX_VALUE;
		GHOST minGhost=null;		
		
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostEdibleTime(ghost)>0)
			{
				int distance=game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
				
				if(distance<minDistance)
				{
					minDistance=distance;
					minGhost=ghost;
				}
			}
		}
		/*if(minGhost!=null)	//we found an edible ghost
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);
		int currentNodeIndex=game.getPacmanCurrentNodeIndex();*/
		
		//get all active pills
		int[] activePills=game.getActivePillsIndices();
		
		//get all active power pills
		int[] activePowerPills=game.getActivePowerPillsIndices();
		
		//create a target array that includes all ACTIVE pills and power pills
		int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];
		
		for(int i=0;i<activePills.length;i++)
			targetNodeIndices[i]=activePills[i];
		
		for(int i=0;i<activePowerPills.length;i++)
			targetNodeIndices[activePills.length+i]=activePowerPills[i];		
		
		//test
		if(minGhost!=null)	//we found an edible ghost
			if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(minGhost))<MIN_DISTANCE)
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);
		int currentNodeIndex=game.getPacmanCurrentNodeIndex();
		//test
		//return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,targetNodeIndices,DM.PATH),DM.PATH);
	}
}
