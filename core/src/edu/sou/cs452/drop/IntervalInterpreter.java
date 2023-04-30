package edu.sou.cs452.drop;

import edu.sou.cs452.drop.Stmt.Rotation;
import edu.sou.cs452.drop.Stmt.Init;
import edu.sou.cs452.drop.IntervalLattice.Interval;

import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class IntervalInterpreter implements Stmt.Visitor<Void>, AbstractInterpreter {
    // The abstract element that represents the result of the analysis.
    private Interval xInterval;
    private Interval yInterval;

    public Interval getXInterval() {
        return xInterval;
    }

    public Interval getYInterval() {
        return yInterval;
    }

    // === BEGIN: INTERPRETER METHODS ===

    void interpret(List<Stmt> statements) {
        for (Stmt statement : statements) {
            execute(statement);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    // In a static analysis, we visit both branches of an OR.
    public Void visitOrStmt(Stmt.Or stmt) {
        for (Stmt s : stmt.left) {
            execute(s);
        }
        for (Stmt s : stmt.right) {
            execute(s);
        }

        return null;
    }

    @Override
    public Void visitIterStmt(Stmt.Iter stmt) {
        Interval initialXInterval;
        Interval initialYInterval;

        // Iterate until a fixpoint is reached (xInterval and yInterval do not change).
        int loopCounter = 0;
        do {
            initialXInterval = xInterval;
            initialYInterval = yInterval;

            // Execute the statements in the loop.
            for (Stmt s : stmt.body) {
                execute(s);
            }

            // If we have been in this loop for a long time, then give up and widen to float -inf inf
            if (++loopCounter > 100) {
                xInterval = new Interval(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
                yInterval = new Interval(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
                break;
            }

            // Check if the intervals have changed.
        } while (!xInterval.equals(initialXInterval) || !yInterval.equals(initialYInterval));

        return null;
    }

    @Override
    public Void visitTranslationStmt(Stmt.Translation stmt) {
        Interval uInterval = IntervalLattice.alpha(stmt.u);
        Interval vInterval = IntervalLattice.alpha(stmt.v);

        xInterval = IntervalLattice.join(xInterval, IntervalLattice.translate(xInterval, uInterval));
        yInterval = IntervalLattice.join(yInterval, IntervalLattice.translate(yInterval, vInterval));

        return null;
    }

    @Override
    public Void visitRotationStmt(Rotation stmt) {
        // Perform rotation on the x and y interval points (4 points total).
        float angleRadians = (float) Math.toRadians(stmt.angle);

        float[] xPoints = {
                xInterval.lowerBound * (float) Math.cos(angleRadians)
                        - yInterval.lowerBound * (float) Math.sin(angleRadians),
                xInterval.lowerBound * (float) Math.cos(angleRadians)
                        - yInterval.upperBound * (float) Math.sin(angleRadians),
                xInterval.upperBound * (float) Math.cos(angleRadians)
                        - yInterval.lowerBound * (float) Math.sin(angleRadians),
                xInterval.upperBound * (float) Math.cos(angleRadians)
                        - yInterval.upperBound * (float) Math.sin(angleRadians)
        };

        float[] yPoints = {
                xInterval.lowerBound * (float) Math.sin(angleRadians)
                        + yInterval.lowerBound * (float) Math.cos(angleRadians),
                xInterval.lowerBound * (float) Math.sin(angleRadians)
                        + yInterval.upperBound * (float) Math.cos(angleRadians),
                xInterval.upperBound * (float) Math.sin(angleRadians)
                        + yInterval.lowerBound * (float) Math.cos(angleRadians),
                xInterval.upperBound * (float) Math.sin(angleRadians)
                        + yInterval.upperBound * (float) Math.cos(angleRadians)
        };

        // Generate a new interval that includes the 4 rotated points.
        float xMin = Math.min(Math.min(xPoints[0], xPoints[1]), Math.min(xPoints[2], xPoints[3]));
        float xMax = Math.max(Math.max(xPoints[0], xPoints[1]), Math.max(xPoints[2], xPoints[3]));
        float yMin = Math.min(Math.min(yPoints[0], yPoints[1]), Math.min(yPoints[2], yPoints[3]));
        float yMax = Math.max(Math.max(yPoints[0], yPoints[1]), Math.max(yPoints[2], yPoints[3]));

        // Update xInterval and yInterval with the new overapproximated intervals.
        this.xInterval = IntervalLattice.join(this.xInterval, new Interval(xMin, xMax));
        this.yInterval = IntervalLattice.join(this.yInterval, new Interval(yMin, yMax));

        return null;
    }

    @Override
    public Void visitInitStmt(Init stmt) {
        Interval p1XInterval = IntervalLattice.alpha(stmt.p1.x);
        Interval p1YInterval = IntervalLattice.alpha(stmt.p1.y);
        Interval p2XInterval = IntervalLattice.alpha(stmt.p2.x);
        Interval p2YInterval = IntervalLattice.alpha(stmt.p2.y);

        Interval xInterval = IntervalLattice.join(p1XInterval, p2XInterval);
        Interval yInterval = IntervalLattice.join(p1YInterval, p2YInterval);

        this.xInterval = xInterval;
        this.yInterval = yInterval;

        return null;
    }

    @Override
    public Rectangle getOverapproximationRectangle() {
        return new Rectangle(xInterval.lowerBound, yInterval.lowerBound, xInterval.upperBound - xInterval.lowerBound,
                yInterval.upperBound - yInterval.lowerBound);
    }

}
