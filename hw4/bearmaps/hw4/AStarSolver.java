package bearmaps.hw4;

import bearmaps.proj2ab.DoubleMapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.List;

import static bearmaps.hw4.SolverOutcome.*;


public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private List<Vertex> solution;
    private double timeSpent;
    private int num;
    private double solutionWeight;
    private SolverOutcome outcome;
    private HashMap<Vertex, Double> distTo = new HashMap<>();
    private HashMap<Vertex, Vertex> edgeTo = new HashMap<>();
    private DoubleMapPQ<Vertex> pq = new DoubleMapPQ<>();


    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch sw = new Stopwatch();
        solution = new ArrayList<>(0);

        if (start.equals(end)) {
            solution.add(start);
            solutionWeight = 0;
            num = 0;
            outcome = SOLVED;
            timeSpent = sw.elapsedTime();
            return;
        }

        List<WeightedEdge<Vertex>> neighborEdges;
        distTo.put(start, (double) 0);
        edgeTo.put(start, start);
        pq.add(start, distTo.get(start) + input.estimatedDistanceToGoal(start, end));


        while (pq.size() > 0 && !pq.getSmallest().equals(end) && timeSpent <= timeout) {
            num++;
            neighborEdges = input.neighbors(pq.removeSmallest());
            for (WeightedEdge<Vertex> e : neighborEdges) {
                relax(e, end, input);
            }
        }

        if (pq.size() == 0) {
            outcome = UNSOLVABLE;
            solutionWeight = 0;
        } else if (timeSpent > timeout) {
            outcome = TIMEOUT;
            solutionWeight = 0;
        } else {
            outcome = SOLVED;
        }

        if (outcome == TIMEOUT  || outcome == UNSOLVABLE) {
            solutionWeight = 0;
        } else {
            solutionWeight = distTo.get(end);
        }

        if (outcome != TIMEOUT && outcome != UNSOLVABLE) {
            Vertex parent = edgeTo.get(end);
            solution.add(end);
            while (!parent.equals(start)) {
                solution.add(0, parent);
                parent = edgeTo.get(parent);
            }
            solution.add(0, start);
        }

        timeSpent = sw.elapsedTime();
    }



    private void relax(WeightedEdge<Vertex> e, Vertex goal, AStarGraph<Vertex> input) {
        Vertex p = e.from();
        Vertex q = e.to();
        double w = e.weight();

        if (!distTo.containsKey(q)) {
            distTo.put(q, Double.POSITIVE_INFINITY);
        }
        if (distTo.get(p) + w < distTo.get(q)) {
            distTo.put(q, distTo.get(p) + w);
            edgeTo.put(q, p);
            if (pq.contains(q)) {
                pq.changePriority(q, distTo.get(q) + input.estimatedDistanceToGoal(q, goal));
            } else if (!pq.contains(q)) {
                pq.add(q, distTo.get(q) + input.estimatedDistanceToGoal(q, goal));
            }

        }
    }


    public SolverOutcome outcome() {
        return outcome;
    }
    public List<Vertex> solution() {
        return solution;
    }
    public double solutionWeight() {
        return solutionWeight;
    }
    public int numStatesExplored() {
        return num;
    }
    public double explorationTime() {
        return timeSpent;
    }
}

