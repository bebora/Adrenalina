package it.polimi.se2019.model.cards;


import it.polimi.se2019.model.ThreeState;

public class Move {

	/**
	 * Select the Object to move:
	 * 	SELF: move the current player
	 * 	PERSPECTIVE: move the perspective of the current player
	 * 	TARGETSOURCE: move the player(s) selected with targetSource
	 */
	private ObjectToMove objectToMove;

	/**
	 * Specify where selected players can go
	 */
	private Target targetDestination;

	/**
	 * Select where moved player will go. TRUE: targetPlayers
	 * FALSE: blackListPLayers
	 * OPTIONAL: nowhere
	 */
	private ThreeState targeting;

	/**
	 * Select players that can be selected to be moved
	 */
	private Target targetSource;

	/**
	 * Prompt string that may be associated with some special moves
	 * e.g. when moving the player perspective
	 */
	private String prompt;

	public static class Builder{
		private ObjectToMove objectToMove = ObjectToMove.SELF;
		private Target targetDestination = new Target.Builder().build();
		private ThreeState targeting;
		private Target targetSource = new Target.Builder().build();
		private String prompt;

		public Builder setObjectToMove(ObjectToMove objectToMove) {
			this.objectToMove = objectToMove;
			return this;
		}

		public Builder setPrompt(String prompt) {
			this.prompt = prompt;
			return this;
		}

		public Builder setTargetDestination(Target targetDestination) {
			this.targetDestination = targetDestination;
			return this;
		}

		public Builder setTargeting(ThreeState targeting) {
			this.targeting = targeting;
			return this;
		}

		public Builder setTargetSource(Target targetSource) {
			this.targetSource = targetSource;
			return this;
		}
		public Move build() {
			return new Move(this);
		}
	}

	public Move(Builder builder) {
		this.objectToMove = builder.objectToMove;
		this.prompt = builder.prompt;
		this.targetDestination = builder.targetDestination;
		this.targeting = builder.targeting;
		this.targetSource = builder.targetSource;
	}
	public Target getTargetDestination() {
		return targetDestination;
	}

	public void setTargetDestination(Target targetDestination) {
		this.targetDestination = targetDestination;
	}

	public ThreeState getTargeting() {
		return targeting;
	}

	public void setTargeting(ThreeState targeting) {
		this.targeting = targeting;
	}

	public Target getTargetSource() {
		return targetSource;
	}

	public void setTargetSource(Target targetSource) {
		this.targetSource = targetSource;
	}

	public void setObjectToMove(ObjectToMove objectToMove) {
		this.objectToMove = objectToMove;
	}

	public ObjectToMove getObjectToMove(){ return this.objectToMove; }

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPrompt() {
		return prompt;
	}
}