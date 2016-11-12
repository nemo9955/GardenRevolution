package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

public class SearchAlg {

	Array<SANode> nodeField = new Array<>(true, 20, SANode.class);
	SANode goalPoint = null;
	Array<SANode> startingPoints = new Array<>(5);
	private int tries;

	public Array<Array<SANode>> getPaths(int minNodeJump) {
		Array<Array<SANode>> ret = new Array<Array<SANode>>(startingPoints.size);

		for (SANode start : startingPoints) {
			Array<SANode> s = new Array<SANode>(minNodeJump + 2);

			tries = -1;
			while (s.size <= 1) {
				tries++;
				for (SANode nod : getNodeField())
					nod.descoperit = false;
				s.clear();
				recPathSearch(s, start, 0, minNodeJump);

				s.add(start);
				s.reverse();
			}

			ret.add(s);
		}

		return ret;
	}

	boolean recPathSearch(Array<SANode> path, SANode root, int depth, int minDepth) {

		int size = root.getNeighbors().size;

		IntArray r = new IntArray(size);
		for (int i = 0; i < size; i++)
			r.add(i);
		for (int i = 0; i < tries; i++)
			r.swap(i, r.random());

		// r.shuffle();
		if (MathUtils.random() > 0.1f)
			r.reverse();

		for (int i = 0; i < size; i++) {
			// SANode vec = root.getNeighbors().get(i);
			SANode vec = root.getNeighbors().get(r.pop());

			if (vec == goalPoint && depth > minDepth) {
				path.add(vec);
				return true;
			}

			if (vec.descoperit == false) {
				vec.descoperit = true;
				if (recPathSearch(path, vec, depth + 1, minDepth)) {
					path.add(vec);
					return true;
				}
				// vec.descoperit = false;
			}
		}
		// root.descoperit = false;

		return false;
	}

	public void setGoal(SANode start) {
		goalPoint = start;
		addFieldPoint(goalPoint);
	}

	public Array<SANode> getNodeField() {
		return nodeField;
	}

	public Array<SANode> getStartingPoints() {
		return startingPoints;
	}

	public SANode getGoalPoint() {
		return goalPoint;
	}

	public void addStarting(SANode goal) {
		startingPoints.add(goal);
		addFieldPoint(goal);
	}

	public void addFieldPoint(SANode node) {
		nodeField.add(node);
	}

	public static class SANode {
		Vector3 point = new Vector3();
		boolean descoperit = false;
		Array<SANode> neighbors = new Array<>(6);

		public SANode(Vector3 point) {
			super();
			this.point.set(point);
		}

		public Vector3 getPoint() {
			return point;
		}

		public Array<SANode> getNeighbors() {
			return neighbors;
		}

		public void addNeighbor(SANode node) {
			neighbors.add(node);
		}
	}

}
